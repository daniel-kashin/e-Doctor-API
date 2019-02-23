package com.edoctor.api.configuration.security

import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging.logger
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsRepository : UserDetailsService {

    private val log = logger { }

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    override fun loadUserByUsername(email: String): UserDetails {
        val user = patientRepository.findByEmail(email)
                ?: doctorRepository.findByEmail(email)

        if (user != null) {
            log.info { "successfully fetched user: user = $user"}
        } else {
            log.info { "cannot fetch user: email = $email"}
            throw UsernameNotFoundException("")
        }

        return User(user.email, user.password, listOf(SimpleGrantedAuthority("USER")))
    }

}