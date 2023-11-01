package com.booster.entity.util

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*


@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class AuditLog {
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private val createdAt: Date? = null

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = true)
    private val updatedAt: Date? = null
}