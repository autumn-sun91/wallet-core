package com.wallet.application.point.port.inbound

interface GrantPointUseCase {
    fun grant(command: GrantPointCommand)

    data class GrantPointCommand(
        val userId: Long,
        val amount: Long,
        val type: String,
        val referKey: String?,
    )
}
