#!/bin/bash
set -e

# ─── 색상 ─────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log()   { echo -e "${GREEN}[$(date +'%H:%M:%S')] $1${NC}"; }
warn()  { echo -e "${YELLOW}[$(date +'%H:%M:%S')] $1${NC}"; }
error() { echo -e "${RED}[$(date +'%H:%M:%S')] $1${NC}"; exit 1; }

# ─── 경로 설정 ────────────────────────────────────
INFRA_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$INFRA_DIR/.." && pwd)"
COMPOSE_FILE="$INFRA_DIR/docker-compose.yml"
ENV_FILE="$INFRA_DIR/.env"
PROJECT_NAME="wallet"

# ─── 기본값 ──────────────────────────────────────
POINT_SCALE=2
BUILD=true
CLEAN=false

# ─── 사용법 ──────────────────────────────────────
usage() {
    echo "Usage: $0 [options]"
    echo "  -p <num>   point-service scale  (default: $POINT_SCALE)"
    echo "  -b         빌드 스킵"
    echo "  -C         전체 초기화"
    echo "  -h         도움말"
    exit 0
}

while getopts "p:bCh" opt; do
    case $opt in
        p) POINT_SCALE=$OPTARG ;;
        b) BUILD=false ;;
        C) CLEAN=true ;;
        h) usage ;;
        *) usage ;;
    esac
done

# ─── docker-compose 래퍼 ─────────────────────────
dc() {
    docker-compose -p "$PROJECT_NAME" -f "$COMPOSE_FILE" "$@"
}

# ─── healthcheck 대기 함수 ────────────────────────
wait_healthy() {
    local service=$1
    local expected=${2:-1}
    local retries=30

    log "$service healthcheck 대기 중..."
    until [ "$(dc ps "$service" | grep -c "healthy")" -ge "$expected" ]; do
        retries=$((retries - 1))
        [ $retries -le 0 ] && warn "$service healthcheck 타임아웃" && break
        echo -n "."; sleep 3
    done
    echo ""
    log "$service 준비 완료"
}

# ─── 사전 검사 ────────────────────────────────────
log "사전 환경 검사 중..."
command -v docker         &>/dev/null || error "docker 미설치"
command -v docker-compose &>/dev/null || error "docker-compose 미설치"
[ -f "$COMPOSE_FILE" ]   || error "docker-compose.yml 없음: $COMPOSE_FILE"

# ─── 전체 초기화 ──────────────────────────────────
if [ "$CLEAN" = true ]; then
    warn "전체 초기화 시작"
    dc stop                     2>/dev/null || true
    dc rm -f                    2>/dev/null || true
    dc down -v --remove-orphans 2>/dev/null || true
    docker builder prune -f     2>/dev/null || true
    log "초기화 완료"
fi

# ─── Spring Boot Jib 빌드 ────────────────────────
if [ "$BUILD" = true ]; then
    log "Point Service jib 빌드 중..."
    cd "$PROJECT_DIR"
    ./gradlew :bootstrap:point-bootstrap:jibDockerBuild
    cd "$INFRA_DIR"
    log "빌드 완료"
fi

# ─── MySQL Master 기동 ───────────────────────────
log "MySQL Master 기동 중..."
dc up -d --no-deps mysql-master
wait_healthy mysql-master 1

# ─── MySQL Slave 기동 ────────────────────────────
log "MySQL Slave 기동 중..."
dc up -d --no-deps mysql-slave-1 mysql-slave-2 mysql-slave-3
wait_healthy mysql-slave-1 1
wait_healthy mysql-slave-2 1
wait_healthy mysql-slave-3 1

# ─── MySQL Replication 초기화 ────────────────────
log "MySQL Replication 초기화 중..."
dc up --no-deps mysql-replication-init
log "MySQL Replication 초기화 완료"

# ─── Point Service 기동 ──────────────────────────
log "Point Service ${POINT_SCALE}개 기동 중..."
dc up -d --no-deps --scale point-service=$POINT_SCALE point-service
wait_healthy point-service $POINT_SCALE

# ─── Nginx 기동 ──────────────────────────────────
log "Nginx 기동 중..."
dc up -d --no-deps nginx-point
log "Nginx 기동 완료"

# ─── Locust 기동 ─────────────────────────────────
log "Locust 기동 중..."
dc up -d --no-deps locust-master
dc up -d --no-deps locust-worker-1 locust-worker-2 locust-worker-3
log "Locust 기동 완료"

# ─── 결과 출력 ───────────────────────────────────
echo ""
log "=============================================="
log " 배포 완료 (project: ${PROJECT_NAME})"
log "=============================================="
echo -e " API              : ${GREEN}http://localhost${NC}"
echo -e " Locust UI        : ${GREEN}http://localhost:8089${NC}"
echo -e " Point Service    : ${GREEN}x${POINT_SCALE}${NC}"
echo ""
dc ps