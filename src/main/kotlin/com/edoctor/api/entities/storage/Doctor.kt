package com.edoctor.api.entities.storage

import com.edoctor.api.entities.storage.base.User
import com.edoctor.api.util.NoArg
import javax.persistence.*

@NoArg
@Entity
@Table(name = "doctors")
class Doctor(
        @OneToMany(mappedBy = "doctor", cascade = [CascadeType.ALL])
        val conversations: Set<Conversation>,

        email: String,
        password: String
) : User(email, password)