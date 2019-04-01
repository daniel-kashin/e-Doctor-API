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

        fullName: String?,

        city: String?,

        email: String,

        password: String,

        @OneToMany(mappedBy = "doctor", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val conversations: MutableSet<ConversationEntity>

) : UserEntity(givenUuid, fullName, city, email, password)