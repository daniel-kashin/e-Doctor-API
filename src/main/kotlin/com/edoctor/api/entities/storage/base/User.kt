package com.edoctor.api.entities.storage.base

import com.edoctor.api.util.NoArg
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@NoArg
@MappedSuperclass
abstract class User(
        @Column(nullable = false, unique = true)
        val email: String,

        @Column(nullable = false)
        val password: String
) : RandomUuidEntity()