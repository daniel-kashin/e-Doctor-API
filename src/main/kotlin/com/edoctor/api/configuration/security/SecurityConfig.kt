package com.edoctor.api.configuration.security

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.authentication.dao.DaoAuthenticationProvider


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var authenticationProvider: DaoAuthenticationProvider

    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
    }

    override fun configure(web: WebSecurity) {
        web
                .ignoring()
                .antMatchers("/register")
                .and()
                .ignoring()
                .antMatchers("/login")
                .and()
                .ignoring()
                .antMatchers("/images/*")
    }

    @Autowired
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
                .userDetailsService<UserDetailsRepository>(userDetailsRepository)
                .passwordEncoder(passwordEncoder)
                .and()
                .authenticationProvider(authenticationProvider)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider() = DaoAuthenticationProvider()
            .apply {
                setUserDetailsService(userDetailsRepository)
                setPasswordEncoder(passwordEncoder)
            }

}