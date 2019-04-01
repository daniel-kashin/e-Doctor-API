package com.edoctor.api.entities.storage.base

import com.edoctor.api.utils.NoArg
import java.util.*
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@NoArg
@MappedSuperclass
abstract class UserEntity(

        givenUuid: UUID?,

        @Column(nullable = true)
        var fullName: String?,

        @Column(nullable = true)
        var city: String?,

        @Column(nullable = false, unique = true)
        val email: String,

        @Column(nullable = false)
        val password: String
) : RandomUuidEntity(givenUuid)