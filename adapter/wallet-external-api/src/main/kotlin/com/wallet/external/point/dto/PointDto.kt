package com.wallet.external.point.dto

import com.wallet.domain.point.PointLedger
import com.wallet.domain.point.vo.PointBalance
import java.time.LocalDateTime

data class GrantPointRequest(
    val userId: Long,
    val amount: Long,
    val type: String,
    val referKey: String?,
)

data class PointBalanceResponse(
    val userId: Long,
    val accAmount: Long,
    val usedAmount: Long,
    val balance: Long,
) {
    companion object {
        fun from(domain: PointBalance): PointBalanceResponse =
            PointBalanceResponse(
                userId = domain.userId,
                accAmount = domain.accAmount,
                usedAmount = domain.usedAmount,
                balance = domain.balance,
            )
    }
}

data class PointLedgerResponse(
    val id: Long?,
    val userId: Long,
    val amount: Long,
    val type: String,
    val referKey: String?,
    val idemKey: String,
    val accAmount: Long,
    val usedAmount: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(domain: PointLedger): PointLedgerResponse =
            PointLedgerResponse(
                id = domain.id,
                userId = domain.userId,
                amount = domain.amount,
                type = domain.type.name,
                referKey = domain.referKey,
                idemKey = domain.idemKey,
                accAmount = domain.accAmount,
                usedAmount = domain.usedAmount,
                createdAt = domain.createdAt,
            )
    }
}

data class PointLedgerListResponse(
    val items: List<PointLedgerResponse>,
    val nextCursor: Long?,
    val hasNext: Boolean,
) {
    companion object {
        fun from(
            ledgers: List<PointLedger>,
            limit: Int,
        ): PointLedgerListResponse {
            val hasNext = ledgers.size > limit
            val items = if (hasNext) ledgers.dropLast(1) else ledgers // 마지막 1개 제거

            return PointLedgerListResponse(
                items = items.map { PointLedgerResponse.from(it) },
                nextCursor = if (hasNext) items.lastOrNull()?.id else null,
                hasNext = hasNext,
            )
        }
    }
}
