package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.RandomUuidEntity
import com.edoctor.api.utils.NoArg
import java.util.*
import javax.persistence.*

@NoArg
@Entity
@Table(name = "medical_accesses")
class MedicalAccessEntity(

        givenUuid: UUID?,

        @Column(nullable = false)
        val medicalRecordType: Int,

        @Column(nullable = true)
        val customModelName: String? = null,

        @Column(nullable = true)
        val customModelUnit: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "patientUuid", nullable = false)
        val patient: PatientEntity,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "doctorUuid", nullable = false)
        val doctor: DoctorEntity

): RandomUuidEntity(givenUuid)