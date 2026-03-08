package com.wallet.kafka.point.config

data class PointGrantMessage(
    val userId: Long,
    val amount: Long,
    val type: String,
    val referKey: String?,
    val idemKey: String,
)
