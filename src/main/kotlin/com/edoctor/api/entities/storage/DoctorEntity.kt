package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.UserEntity
import com.edoctor.api.utils.NoArg
import com.sun.org.apache.xpath.internal.operations.Bool
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

        email: String,

        password: String,

        @OneToMany(mappedBy = "doctor", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val conversations: MutableSet<ConversationEntity>

) : UserEntity(givenUuid, fullName, city, dateOfBirthTimestamp, isMale, imageName, email, password)