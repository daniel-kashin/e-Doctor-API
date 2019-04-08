package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.RandomUuidEntity
import com.edoctor.api.utils.NoArg
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@NoArg
@Entity
@Table(name = "body_parameters")
class BodyParameterEntity(

        givenUuid: UUID?,

        @Column(nullable = false)
        var measurementTimestamp: Long,

        @Column(nullable = false)
        val type: Int,

        @Column(nullable = false)
        var firstValue: Double,

        @Column(nullable = true)
        var secondValue: Double? = null,

        @Column(nullable = true)
        val customModelName: String? = null,

        @Column(nullable = true)
        val customModelUnit: String? = null

) : RandomUuidEntity(givenUuid)

class BodyParameterEntityType(
        val type: Int,
        val customModelName: String?,
        val customModelUnit: String?
)