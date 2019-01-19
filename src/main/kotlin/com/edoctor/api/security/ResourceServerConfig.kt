package com.edoctor.api.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer

@Configuration
@EnableResourceServer
class ResourceServerConfig : ResourceServerConfigurerAdapter() {

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.resourceId("resource_id").stateless(false);
    }

}