package com.edoctor.api.security

import com.edoctor.api.entities.LoginRequest
import com.edoctor.api.entities.Patient
import com.edoctor.api.entities.User
import com.edoctor.api.exception.NoLogHttpServerErrorExceprion
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
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
    fun register(@RequestBody loginRequest: LoginRequest): User {
        if (userStorage.findUserByEmail(loginRequest.email) != null) {
            throw NoLogHttpServerErrorExceprion(HttpStatus.BAD_REQUEST, "User already exists")
        }

        val user = Patient(UUID.randomUUID().toString(), loginRequest.email, loginRequest.password)

        userStorage.save(user)

        val usernamePasswordToken = UsernamePasswordAuthenticationToken(user.email, loginRequest.password)

        // authenticate token with the given account details
        val authentication = authenticationManager.authenticate(usernamePasswordToken)

        if (authentication.isAuthenticated) {
            // provide authentication info to the context
            SecurityContextHolder.getContext().authentication = usernamePasswordToken
        }

        return user
    }

    @PostMapping("/login")
    fun login(email: String, password: String): User {
        val user = userStorage.findUserByEmail(email)
                ?: throw NoLogHttpServerErrorExceprion(HttpStatus.BAD_REQUEST, "User not exists")

        if (passwordEncoder.matches(password, user.password)) {
            return user
        } else {
            throw NoLogHttpServerErrorExceprion(HttpStatus.BAD_REQUEST, "Wrong password")
        }
    }

}