package com.edoctor.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer

@SpringBootApplication
@EnableResourceServer
@EnableConfigurationProperties
@EntityScan(basePackages = ["com.edoctor.api.entities.storage"])
class ApiApplication

fun main(args: Array<String>) {
	runApplication<ApiApplication>(*args)
}

