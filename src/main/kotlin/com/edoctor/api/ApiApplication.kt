package com.edoctor.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer

@SpringBootApplication
@EnableResourceServer
class ApiApplication

fun main(args: Array<String>) {
	runApplication<ApiApplication>(*args)
}

