package com.edoctor.api.configuration.security

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.config.annotation.web.builders.WebSecurity

@Configuration
@EnableWebSecurity
@Order(2)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var userDetailsService: UserDetailsRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun configure(web: WebSecurity) {
        web
                .ignoring()
                .antMatchers("/register")
                .and()
                .ignoring()
                .antMatchers("/login")
    }

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth
                .userDetailsService<UserDetailsService>(userDetailsService)
                .passwordEncoder(passwordEncoder)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

    @Bean
    // TODO: replace with encider
    fun passwordEncoder(): PasswordEncoder = NoOpPasswordEncoder.getInstance()

}