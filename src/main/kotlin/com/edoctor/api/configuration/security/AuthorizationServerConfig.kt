package com.edoctor.api.configuration.security

import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore

@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfig : AuthorizationServerConfigurerAdapter() {

    companion object {
        internal const val CLIENT_ID = "client"
        internal const val CLIENT_SECRET = "clientpassword"
        internal const val KEY_TYPE_PASSWORD = "password"
        internal const val KEY_AUTHORIZATION_CODE = "authorization_code"
        internal const val KEY_REFRESH_TOKEN = "refresh_token"
        internal const val KEY_IMPLICIT = "implicit"
        internal const val SCOPE_READ = "read"
        internal const val SCOPE_WRITE = "write"
        internal const val TRUST = "trust"
        internal const val ACCESS_TOKEN_VALIDITY_SECONDS = 1 * 60 * 60 * 24
    }

    @Autowired
    private lateinit var tokenStore: TokenStore

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var userDetailsRepository: UserDetailsRepository

    override fun configure(configurer: ClientDetailsServiceConfigurer) {
        configurer
                .inMemory()
                .withClient(CLIENT_ID).secret(CLIENT_SECRET)
                .authorizedGrantTypes(KEY_TYPE_PASSWORD, KEY_AUTHORIZATION_CODE, KEY_REFRESH_TOKEN, KEY_IMPLICIT)
                .scopes(SCOPE_READ, SCOPE_WRITE, TRUST)
                .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsRepository)
    }

    override fun configure(oauthServer: AuthorizationServerSecurityConfigurer) {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
    }

    // TODO: replace with Jdbc
    @Bean
    fun tokenStore(): TokenStore = InMemoryTokenStore()

}