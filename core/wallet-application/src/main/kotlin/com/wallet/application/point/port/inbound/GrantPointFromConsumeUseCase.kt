package com.wallet.application.point.port.inbound

interface GrantPointFromConsumeUseCase {
    fun grant(command: ConsumePointCommand)

    data class ConsumePointCommand(
        val userId: Long,
        val amount: Long,
        val type: String,
        val referKey: String?,
        val idemKey: String?,
    )
}
