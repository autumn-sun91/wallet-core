package com.wallet.external.point

import com.wallet.application.point.port.inbound.GetPointUseCase
import com.wallet.application.point.port.inbound.GrantPointUseCase
import com.wallet.external.point.dto.GrantPointRequest
import com.wallet.external.point.dto.PointBalanceResponse
import com.wallet.external.point.dto.PointLedgerListResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/points")
class PointRestController(
    private val getPointUseCase: GetPointUseCase,
    private val grantPointUseCase: GrantPointUseCase,
) {
    // ─── 잔액 조회 ────────────────────────────────
    @GetMapping("/{userId}/balance")
    fun getBalance(
        @PathVariable userId: Long,
    ): ResponseEntity<PointBalanceResponse> {
        val balance = getPointUseCase.getBalance(userId)
        return ResponseEntity.ok(PointBalanceResponse.from(balance))
    }

    // ─── 포인트 이력 조회 (커서 기반) ───────────────
    @GetMapping("/{userId}/ledgers")
    fun getLedgers(
        @PathVariable userId: Long,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "20") limit: Int,
    ): ResponseEntity<PointLedgerListResponse> {
        val ledgers =
            getPointUseCase.getLedgers(
                GetPointUseCase.GetLedgerQuery(
                    userId = userId,
                    cursor = cursor,
                    limit = limit,
                ),
            )
        return ResponseEntity.ok(PointLedgerListResponse.from(ledgers, limit))
    }

    @PostMapping("/grant")
    fun grant(
        @RequestBody request: GrantPointRequest,
    ): ResponseEntity<Unit> {
        grantPointUseCase.grant(
            GrantPointUseCase.GrantPointCommand(
                userId = request.userId,
                amount = request.amount,
                type = request.type,
                referKey = request.referKey,
            ),
        )
        return ResponseEntity.ok().build()
    }
}
