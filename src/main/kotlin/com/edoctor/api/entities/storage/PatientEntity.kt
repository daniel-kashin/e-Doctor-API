package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.UserEntity
import com.edoctor.api.utils.NoArg
import java.util.*
import javax.persistence.*

@NoArg
@Entity
@Table(name = "patients")
class PatientEntity constructor(

        givenUuid: UUID?,

        fullName: String? = null,

        city: String? = null,

        dateOfBirthTimestamp: Long? = null,

        isMale: Boolean? = null,

        imageName: String? = null,

        @Column(nullable = true)
        var bloodGroup: Int? = null,

        email: String,

        password: String,

        @OneToMany(mappedBy = "patient", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val conversations: MutableSet<ConversationEntity>,

        @OneToMany(mappedBy = "patient", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val medicalEvents: MutableSet<MedicalEventEntity>,

        @OneToMany(mappedBy = "patient", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val bodyParameters: MutableSet<BodyParameterEntity>,

        @OneToMany(mappedBy = "patient", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val medicalAccesses: MutableSet<MedicalRecordAccessEntity>

) : UserEntity(givenUuid, fullName, city, dateOfBirthTimestamp, isMale, imageName, email, password)