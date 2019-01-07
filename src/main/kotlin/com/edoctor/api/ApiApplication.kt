package com.edoctor.api

import com.edoctor.api.entities.WebSocketConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class ApiApplication : SpringBootServletInitializer() {
	override fun configure(builder: SpringApplicationBuilder) = builder.sources(WebSocketConfiguration::class.java)
}

fun main(args: Array<String>) {
	runApplication<ApiApplication>(*args)
}

