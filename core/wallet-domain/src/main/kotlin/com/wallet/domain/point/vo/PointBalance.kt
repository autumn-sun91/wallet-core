package com.wallet.domain.point.vo

data class PointBalance(
    val userId: Long,
    val accAmount: Long,
    val usedAmount: Long,
) {
    val balance: Long
        get() = accAmount - usedAmount

    companion object {
        fun empty(userId: Long) =
            PointBalance(
                userId = userId,
                accAmount = 0L,
                usedAmount = 0L,
            )
    }
}
