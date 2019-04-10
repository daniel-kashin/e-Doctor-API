package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.UserEntity
import com.edoctor.api.utils.NoArg
import java.util.*
import javax.persistence.*

@NoArg
@Entity
@Table(name = "doctors")
class DoctorEntity constructor(

        givenUuid: UUID?,

        fullName: String? = null,

        city: String? = null,

        dateOfBirthTimestamp: Long? = null,

        isMale: Boolean? = null,

        imageName: String? = null,

        @Column(nullable = true)
        var yearsOfExperience: Int? = null,

        @Column(nullable = true)
        var category: Int? = null,

        @Column(nullable = true)
        var specialization: String? = null,

        @Column(nullable = true)
        var clinicalInterests: String? = null,

        @Column(nullable = true)
        var education: String? = null,

        @Column(nullable = true)
        var workExperience: String? = null,

        @Column(nullable = true)
        var trainings: String? = null,

        email: String,

        password: String,

        @OneToMany(mappedBy = "doctor", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val conversations: MutableSet<ConversationEntity>,

        @OneToMany(mappedBy = "doctor", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val medicalAccesses: MutableSet<MedicalAccessEntity>

) : UserEntity(givenUuid, fullName, city, dateOfBirthTimestamp, isMale, imageName, email, password)