package com.wallet.persistence

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager

class RoutingDataSource : AbstractRoutingDataSource() {
    private val slaveKeys =
        listOf(
            DataSourceType.SLAVE_1,
            DataSourceType.SLAVE_2,
            DataSourceType.SLAVE_3,
        )
    private var slaveIndex = 0

    override fun determineCurrentLookupKey(): Any {
        // readOnly 트랜잭션 → Slave 라운드 로빈
        return if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            slaveKeys[slaveIndex++ % slaveKeys.size]
        } else {
            DataSourceType.MASTER
        }
    }

    enum class DataSourceType { MASTER, SLAVE_1, SLAVE_2, SLAVE_3 }
}
