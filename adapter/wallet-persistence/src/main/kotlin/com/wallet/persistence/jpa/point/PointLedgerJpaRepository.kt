package com.wallet.persistence.jpa.point

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PointLedgerJpaRepository : JpaRepository<PointLedgerJpaEntity, Long> {
    @Query(
        """
        SELECT p FROM PointLedgerJpaEntity p
        WHERE p.userId = :userId
        ORDER BY p.createdAt DESC
        LIMIT 1
    """,
    )
    fun findLatestByUserId(
        @Param("userId") userId: Long,
        pageable: Pageable,
    ): List<PointLedgerJpaEntity>

    // ─── 최초 조회 (cursor 없음) ──────────────────
    @Query(
        """
        SELECT p FROM PointLedgerJpaEntity p
        WHERE p.userId = :userId
        ORDER BY p.id DESC
    """,
    )
    fun findAllByUserId(
        @Param("userId") userId: Long,
        pageable: Pageable,
    ): List<PointLedgerJpaEntity>

    // ─── 커서 이후 조회 ───────────────────────────
    @Query(
        """
        SELECT p FROM PointLedgerJpaEntity p
        WHERE p.userId = :userId
        AND p.id < :cursor
        ORDER BY p.id DESC
    """,
    )
    fun findAllByUserIdWithCursor(
        @Param("userId") userId: Long,
        @Param("cursor") cursor: Long,
        pageable: Pageable,
    ): List<PointLedgerJpaEntity>
}
