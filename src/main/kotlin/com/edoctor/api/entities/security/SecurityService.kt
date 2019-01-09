package com.edoctor.api.entities.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service

@Service
class SecurityService {

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var userDetailsRepository: UserDetailsRepository

    fun register(email: String, password: String) {
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                email,
                password,
                listOf(SimpleGrantedAuthority("USER"))
        )

        authenticationManager.authenticate(usernamePasswordAuthenticationToken)
    }

    fun login(email: String, password: String) {
        val userDetails = userDetailsRepository.loadUserByUsername(email)

        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                email,
                password,
                userDetails.authorities
        )

        authenticationManager.authenticate(usernamePasswordAuthenticationToken)

        if (usernamePasswordAuthenticationToken.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
        }
    }
}