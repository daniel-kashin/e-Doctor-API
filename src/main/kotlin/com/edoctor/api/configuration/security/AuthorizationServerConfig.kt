package com.edoctor.api.configuration.security

import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore

@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfig : AuthorizationServerConfigurerAdapter() {

    companion object {
        internal val CLIENT_ID = "client"
        internal val CLIENT_SECRET = "clientpassword"
        internal val KEY_TYPE_PASSWORD = "password"
        internal val KEY_AUTHORIZATION_CODE = "authorization_code"
        internal val KEY_REFRESH_TOKEN = "refresh_token"
        internal val KEY_IMPLICIT = "implicit"
        internal val SCOPE_READ = "read"
        internal val SCOPE_WRITE = "write"
        internal val TRUST = "trust"
        internal val ACCESS_TOKEN_VALIDITY_SECONDS = 1 * 20
    }

    @Autowired
    private lateinit var tokenStore: TokenStore

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

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
    }

    override fun configure(oauthServer: AuthorizationServerSecurityConfigurer) {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
    }

    @Bean
    fun tokenStore(): TokenStore = InMemoryTokenStore()

}