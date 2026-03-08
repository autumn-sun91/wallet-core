package com.wallet.application.point.port.outbound

import com.wallet.domain.point.PointLedger

interface PointLedgerRepository {
    fun save(pointLedger: PointLedger): PointLedger

    fun getLatestByUserId(userId: Long): PointLedger?

    fun getAllByUserId(
        userId: Long,
        cursor: Long? = null,
        limit: Int,
    ): List<PointLedger>
}
