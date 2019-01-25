package com.edoctor.api.entities.storage.base

import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class User(
        @Column(nullable = false, unique = true)
        val email: String,

        @Column(nullable = false)
        val password: String
) : RandomUuidEntity()