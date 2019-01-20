package com.edoctor.api.security

import com.edoctor.api.entities.LoginRequest
import com.edoctor.api.entities.Patient
import com.edoctor.api.entities.User
import com.edoctor.api.exception.NoLogHttpServerErrorExceprion
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.RequestBody

@RestController
class AuthorizationController {

    @Autowired
    private lateinit var userStorage: UserStorage

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    // TODO
    private lateinit var passwordEncoder: PasswordEncoder

    @PostMapping("/register")
    fun register(@RequestBody loginRequest: LoginRequest): ResponseEntity<User> {
        if (userStorage.findUserByEmail(loginRequest.email) != null) {
            return ResponseEntity(HttpStatus.CONFLICT)
        }

        val user = Patient(UUID.randomUUID().toString(), loginRequest.email, loginRequest.password)

        userStorage.save(user)

        return ResponseEntity.ok(user)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<User> {
        val user = userStorage.findUserByEmail(loginRequest.email)
                ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        if (passwordEncoder.matches(loginRequest.password, user.password)) {
            return ResponseEntity.ok(user)
        } else {
            return ResponseEntity(HttpStatus.CONFLICT)
        }
    }

}