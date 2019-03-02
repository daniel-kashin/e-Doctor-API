package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.User
import com.edoctor.api.utils.NoArg
import java.util.*
import javax.persistence.*

@NoArg
@Entity
@Table(name = "patients")
class Patient constructor(

        givenUuid: UUID?,

        email: String,

        password: String,

        @OneToMany(mappedBy = "patient", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val conversations: MutableSet<Conversation>

) : User(givenUuid, email, password)