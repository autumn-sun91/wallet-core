package com.wallet.persistence.jpa.point

import com.wallet.domain.point.PointLedger
import com.wallet.domain.point.PointType
import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

@Entity
@Table(
    name = "points_ledger",
    indexes = [
        Index(name = "idx_user_created", columnList = "userId, createdAt DESC"),
        Index(name = "idx_refer_key", columnList = "referKey"),
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uq_idem_key", columnNames = ["idemKey"]),
    ],
)
class PointLedgerJpaEntity(
    @Id
    @Tsid
    val id: Long? = null,
    @Column(nullable = false)
    val userId: Long,
    @Column(nullable = false)
    val amount: Long,
    @Column(nullable = false, length = 30)
    val type: String,
    @Column(nullable = true, length = 255)
    val referKey: String?,
    @Column(nullable = false, length = 255)
    val idemKey: String,
    @Column(nullable = false)
    val accAmount: Long,
    @Column(nullable = false)
    val usedAmount: Long,
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun from(domain: PointLedger): PointLedgerJpaEntity =
            PointLedgerJpaEntity(
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

    fun toDomain(): PointLedger =
        PointLedger.of(
            id = id,
            userId = userId,
            amount = amount,
            type = PointType.valueOf(type),
            referKey = referKey,
            idemKey = idemKey,
            accAmount = accAmount,
            usedAmount = usedAmount,
            createdAt = createdAt,
        )
}
