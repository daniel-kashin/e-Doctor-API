package com.edoctor.api.entities.storage.base

import org.springframework.data.domain.Persistable
import java.util.*
import javax.persistence.*

@MappedSuperclass
abstract class RandomUuidEntity(givenId: UUID? = null) : Persistable<String> {

    @Id
    @Column(name = "id", length = 16, unique = true, nullable = false)
    val uuid: String = (givenId ?: UUID.randomUUID()).toString()

    @Transient
    private var persisted: Boolean = givenId != null

    override fun getId(): String = uuid

    override fun isNew(): Boolean = !persisted

    override fun hashCode(): Int = uuid.hashCode()

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other == null -> false
            other !is RandomUuidEntity -> false
            else -> uuid == other.uuid
        }
    }

    @PostPersist
    @PostLoad
    private fun setPersisted() {
        persisted = true
    }
}