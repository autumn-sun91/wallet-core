#!/bin/bash
set -e

MASTER_HOST="mysql-master"
ROOT_PASS="${MYSQL_ROOT_PASSWORD}"
REPL_USER="replicator"
REPL_PASS="repl1234"

mysql_cmd() {
    mysql -h "$1" -u root -p"${ROOT_PASS}" --connect-timeout=10 -e "$2"
}

echo "Replication 초기화 시작..."

# ─── Master: replication 유저 생성 ───────────────
mysql_cmd "$MASTER_HOST" "
    CREATE USER IF NOT EXISTS '${REPL_USER}'@'%' IDENTIFIED WITH mysql_native_password BY '${REPL_PASS}';
    GRANT REPLICATION SLAVE ON *.* TO '${REPL_USER}'@'%';
    FLUSH PRIVILEGES;
"

# ─── Master: binlog 위치 조회 ────────────────────
MASTER_STATUS=$(mysql -h "$MASTER_HOST" -u root -p"${ROOT_PASS}" \
    --connect-timeout=10 -e "SHOW MASTER STATUS\G")

MASTER_LOG_FILE=$(echo "$MASTER_STATUS" | grep "File:" | awk '{print $2}')
MASTER_LOG_POS=$(echo "$MASTER_STATUS"  | grep "Position:" | awk '{print $2}')

echo "Master Log File: $MASTER_LOG_FILE"
echo "Master Log Position: $MASTER_LOG_POS"

# ─── 각 Slave 설정 ────────────────────────────────
for SLAVE_HOST in mysql-slave-1 mysql-slave-2 mysql-slave-3; do
    echo "$SLAVE_HOST Replication 설정 중..."

    mysql_cmd "$SLAVE_HOST" "
        STOP SLAVE;
        CHANGE MASTER TO
            MASTER_HOST='${MASTER_HOST}',
            MASTER_USER='${REPL_USER}',
            MASTER_PASSWORD='${REPL_PASS}',
            MASTER_LOG_FILE='${MASTER_LOG_FILE}',
            MASTER_LOG_POS=${MASTER_LOG_POS};
        START SLAVE;
    "

    # ─── Replication 완료 후 read-only 적용 ───────
    mysql_cmd "$SLAVE_HOST" "
        SET GLOBAL read_only = 1;
        SET GLOBAL super_read_only = 1;
    "

    # ─── Slave 상태 확인 ──────────────────────────
    SLAVE_STATUS=$(mysql -h "$SLAVE_HOST" -u root -p"${ROOT_PASS}" \
        --connect-timeout=10 -e "SHOW SLAVE STATUS\G")

    IO_RUNNING=$(echo "$SLAVE_STATUS"  | grep "Slave_IO_Running:"  | awk '{print $2}')
    SQL_RUNNING=$(echo "$SLAVE_STATUS" | grep "Slave_SQL_Running:" | awk '{print $2}')

    echo "$SLAVE_HOST | IO: $IO_RUNNING | SQL: $SQL_RUNNING"
done

echo "Replication 초기화 완료"