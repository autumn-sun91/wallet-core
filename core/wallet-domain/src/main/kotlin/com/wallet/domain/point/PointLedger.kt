package com.wallet.domain.point

import com.wallet.domain.common.ULIDGenerator
import java.math.BigDecimal
import java.time.LocalDateTime

class PointLedger private constructor(
    val id: Long? = null,
    val userId: Long,
    val amount: Long,
    val type: PointType,
    val referKey: String?,
    val idemKey: String,
    val accAmount: Long,
    val usedAmount: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun of(
            id: Long?,
            userId: Long,
            amount: Long,
            type: PointType,
            referKey: String?,
            idemKey: String,
            accAmount: Long,
            usedAmount: Long,
            createdAt: LocalDateTime,
        ): PointLedger =
            PointLedger(
                id = id,
                userId = userId,
                amount = amount,
                type = type,
                referKey = referKey,
                idemKey = idemKey,
                accAmount = accAmount,
                usedAmount = usedAmount,
                createdAt = createdAt,
            )

        fun deposit(
            userId: Long,
            amount: Long,
            type: PointType,
            referKey: String?,
            prevAccAmount: Long,
            prevUsedAmount: Long,
            idemKey: String? = null,
        ): PointLedger {
            require(amount > 0L) { "적립 금액은 0보다 커야 합니다." }
            require(type.isDeposit()) { "적립 타입이 아닙니다: $type" }

            return PointLedger(
                userId = userId,
                amount = amount,
                type = type,
                referKey = referKey,
                idemKey = idemKey ?: "DEPOSIT-${ULIDGenerator.generate()}",
                accAmount = prevAccAmount + amount,
                usedAmount = prevUsedAmount,
            )
        }

        fun withdraw(
            userId: Long,
            amount: Long,
            type: PointType,
            referKey: String?,
            prevAccAmount: Long,
            prevUsedAmount: Long,
            idemKey: String? = null,
        ): PointLedger {
            require(amount > 0L) { "차감 금액은 0보다 커야 합니다." }
            require(type.isWithdraw()) { "차감 타입이 아닙니다: $type" }

            val balance = prevAccAmount - prevUsedAmount
            require(balance >= amount) { "잔액이 부족합니다. 현재 잔액: $balance" }

            return PointLedger(
                userId = userId,
                amount = BigDecimal.valueOf(amount).negate().toLong(),
                type = type,
                referKey = referKey,
                idemKey = idemKey ?: "WITHDRAW-${ULIDGenerator.generate()}",
                accAmount = prevAccAmount,
                usedAmount = prevUsedAmount + amount,
            )
        }
    }
}
