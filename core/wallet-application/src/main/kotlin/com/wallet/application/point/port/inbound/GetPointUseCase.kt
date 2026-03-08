package com.wallet.application.point.port.inbound

import com.wallet.domain.point.PointLedger
import com.wallet.domain.point.vo.PointBalance

interface GetPointUseCase {
    // 현재 잔액 조회
    fun getBalance(userId: Long): PointBalance

    // 포인트 이력 조회 (커서 기반)
    fun getLedgers(query: GetLedgerQuery): List<PointLedger>

    data class GetLedgerQuery(
        val userId: Long,
        val cursor: Long?, // 마지막 id
        val limit: Int = 20,
    )
}
