package com.edoctor.api.controller

import com.edoctor.api.entities.LoginRequest
import com.edoctor.api.entities.Patient
import com.edoctor.api.entities.User
import com.edoctor.api.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.web.bind.annotation.RequestBody

@RestController
class AuthorizationController {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    // TODO
    private lateinit var passwordEncoder: PasswordEncoder

    @PostMapping("/register")
    fun register(@RequestBody loginRequest: LoginRequest): ResponseEntity<User> {
        if (userRepository.findById(loginRequest.email).isPresent) {
            return ResponseEntity(HttpStatus.CONFLICT)
        }

        val user = Patient(UUID.randomUUID().toString(), loginRequest.email, loginRequest.password)

        userRepository.save(user)

        return ResponseEntity.ok(user)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<User> {
        val userOptional = userRepository.findById(loginRequest.email)
        if (!userOptional.isPresent) return ResponseEntity(HttpStatus.BAD_REQUEST)

        val user = userOptional.get()

        if (passwordEncoder.matches(loginRequest.password, user.password)) {
            return ResponseEntity.ok(user)
        } else {
            return ResponseEntity(HttpStatus.CONFLICT)
        }
    }

}