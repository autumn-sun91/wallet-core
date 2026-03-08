package com.wallet.application.point.service

import com.wallet.application.point.port.inbound.GetPointUseCase
import com.wallet.application.point.port.inbound.GrantPointFromConsumeUseCase
import com.wallet.application.point.port.inbound.GrantPointUseCase
import com.wallet.application.point.port.outbound.PointLedgerRepository
import com.wallet.domain.point.PointLedger
import com.wallet.domain.point.PointType
import com.wallet.domain.point.vo.PointBalance
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class PointService(
    private val pointLedgerRepository: PointLedgerRepository,
) : GrantPointUseCase,
    GetPointUseCase,
    GrantPointFromConsumeUseCase {
    @Transactional(readOnly = true)
    override fun getBalance(userId: Long): PointBalance {
        val latest =
            pointLedgerRepository.getLatestByUserId(userId)
                ?: return PointBalance.empty(userId)

        return PointBalance(
            userId = userId,
            accAmount = latest.accAmount,
            usedAmount = latest.usedAmount,
        )
    }

    @Transactional(readOnly = true)
    override fun getLedgers(query: GetPointUseCase.GetLedgerQuery): List<PointLedger> =
        pointLedgerRepository.getAllByUserId(
            userId = query.userId,
            cursor = query.cursor,
            limit = query.limit + 1,
        )

    @Transactional
    override fun grant(command: GrantPointUseCase.GrantPointCommand) {
        process(
            userId = command.userId,
            amount = command.amount,
            type = command.type,
            referKey = command.referKey,
        )
    }

    @Transactional
    override fun grant(command: GrantPointFromConsumeUseCase.ConsumePointCommand) {
        process(
            userId = command.userId,
            amount = command.amount,
            type = command.type,
            referKey = command.referKey,
            idemKey = command.idemKey,
        )
    }

    private fun process(
        userId: Long,
        amount: Long,
        type: String,
        referKey: String?,
        idemKey: String? = null,
    ) {
        val pointType = PointType.valueOf(type)

        val prevLedger = pointLedgerRepository.getLatestByUserId(userId)

        val newLedger =
            when {
                pointType.isDeposit() ->
                    PointLedger.deposit(
                        userId = userId,
                        amount = amount,
                        type = pointType,
                        referKey = referKey,
                        prevAccAmount = prevLedger?.accAmount ?: 0L,
                        prevUsedAmount = prevLedger?.usedAmount ?: 0L,
                        idemKey = idemKey,
                    )
                pointType.isWithdraw() ->
                    PointLedger.withdraw(
                        userId = userId,
                        amount = amount,
                        type = pointType,
                        referKey = referKey,
                        prevAccAmount = prevLedger?.accAmount ?: 0L,
                        prevUsedAmount = prevLedger?.usedAmount ?: 0L,
                        idemKey = idemKey,
                    )
                else -> throw IllegalArgumentException("처리할 수 없는 포인트 타입입니다: $pointType")
            }

        pointLedgerRepository.save(newLedger)
    }
}
