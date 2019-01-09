package com.edoctor.api.entities.security

import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.security.core.userdetails.User as SpringUser

@Service
class UserDetailsRepository : UserDetailsService {

    @Autowired
    private lateinit var userStorage: UserStorage

    override fun loadUserByUsername(email: String): UserDetails {
        val user = userStorage.findUserByEmail(email) ?: throw UsernameNotFoundException("")

        return SpringUser(user.email, user.password, listOf(SimpleGrantedAuthority("USER")))
    }

}