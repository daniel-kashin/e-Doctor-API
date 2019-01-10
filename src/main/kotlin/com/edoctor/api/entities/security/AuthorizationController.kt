package com.edoctor.api.entities.security

import com.edoctor.api.entities.Patient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorizationController {

    @Autowired
    private lateinit var userStorage: UserStorage

    @PostMapping("/register")
    fun index(@RequestBody user: Patient) {
        userStorage.save(user)
    }

}