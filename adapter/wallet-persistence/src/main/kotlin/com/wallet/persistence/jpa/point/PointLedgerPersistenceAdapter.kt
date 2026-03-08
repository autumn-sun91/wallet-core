package com.wallet.persistence.jpa.point

import com.wallet.application.point.port.outbound.PointLedgerRepository
import com.wallet.domain.point.PointLedger
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class PointLedgerPersistenceAdapter(
    private val pointLedgerJpaRepository: PointLedgerJpaRepository,
) : PointLedgerRepository {
    override fun save(pointLedger: PointLedger): PointLedger =
        pointLedgerJpaRepository.save(PointLedgerJpaEntity.from(pointLedger)).toDomain()

    override fun getLatestByUserId(userId: Long): PointLedger? =
        pointLedgerJpaRepository
            .findLatestByUserId(userId, PageRequest.of(0, 1))
            .firstOrNull()
            ?.toDomain()

    override fun getAllByUserId(
        userId: Long,
        cursor: Long?,
        limit: Int,
    ): List<PointLedger> =
        if (cursor == null) {
            pointLedgerJpaRepository.findAllByUserId(userId, PageRequest.of(0, limit))
        } else {
            pointLedgerJpaRepository.findAllByUserIdWithCursor(userId, cursor, PageRequest.of(0, limit))
        }.map { it.toDomain() }
}
