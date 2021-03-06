package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.RandomUuidEntity
import com.edoctor.api.utils.NoArg
import java.util.*
import javax.persistence.*

@NoArg
@Entity
@Table(name = "body_parameters")
class BodyParameterEntity(

        givenUuid: UUID?,

        @Column(nullable = false)
        val type: Int,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "patientUuid", nullable = false)
        val patient: PatientEntity,

        @Column(nullable = false)
        val updateTimestamp: Long,

        @Column(nullable = false)
        val deleted: Boolean,

        @Column(nullable = false)
        var measurementTimestamp: Long,

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