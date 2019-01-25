package com.edoctor.api.configuration.security

import com.edoctor.api.repositories.PatientRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsRepository : UserDetailsService {

    @Autowired
    private lateinit var patientRepository: PatientRepository

    override fun loadUserByUsername(email: String): UserDetails {
        val user = patientRepository.findById(email).orElseThrow { throw UsernameNotFoundException("") }

        return User(user.email, user.password, listOf(SimpleGrantedAuthority("USER")))
    }

}