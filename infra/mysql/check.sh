#!/bin/bash

for i in 1 2 3; do
    echo "===== Slave $i ====="
    docker exec -it wallet-mysql-slave-$i mysql -u root -proot1234 \
        -e "SHOW SLAVE STATUS\G" 2>/dev/null | \
        grep -E "Slave_IO_Running|Slave_SQL_Running|Seconds_Behind_Master|Last_Error|Master_Host"
done