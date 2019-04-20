package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.RandomUuidEntity
import com.edoctor.api.utils.NoArg
import java.util.*
import javax.persistence.*

@NoArg
@Entity
@Table(name = "medical_events")
class MedicalEventEntity(

        givenUuid: UUID?,

        @Column(nullable = false)
        val type: Int,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "patientUuid", nullable = false)
        val patient: PatientEntity,

        @Column(nullable = false)
        val isDeleted: Boolean,

        @Column(nullable = false)
        val updateTimestamp: Long,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "doctorUuid", nullable = true)
        val doctorCreator: DoctorEntity?,

        @Column(nullable = false)
        var isAddedFromDoctor: Boolean = false,

        @Column(nullable = false)
        var timestamp: Long,

        @Column(nullable = true)
        var endTimestamp: Long? = null,

        @Column(nullable = true)
        var name: String? = null,

        @Column(nullable = true)
        var clinic: String? = null,

        @Column(nullable = true)
        var doctorName: String? = null,

        @Column(nullable = true)
        var doctorSpecialization: String? = null,

        @Column(nullable = true)
        var symptoms: String? = null,

        @Column(nullable = true)
        var diagnosis: String? = null,

        @Column(nullable = true)
        var recipe: String? = null,

        @Column(nullable = true)
        var comment: String? = null

) : RandomUuidEntity(givenUuid)

class MedicalEventEntityType(
        val type: Int
)