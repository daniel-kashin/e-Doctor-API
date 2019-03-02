package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.User
import com.edoctor.api.utils.NoArg
import java.util.*
import javax.persistence.*

@NoArg
@Entity
@Table(name = "doctors")
class Doctor constructor(

        givenUuid: UUID?,

        email: String,

        password: String,

        @OneToMany(mappedBy = "doctor", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val conversations: MutableSet<Conversation>

) : User(givenUuid, email, password)