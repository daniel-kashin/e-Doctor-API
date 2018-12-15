package com.edoctor.api

import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {

    val log = logger { }

    @Autowired
    lateinit var helloWorldService: HelloWorldService

    @GetMapping("/")
    fun index(@RequestParam(value = "name") name: String) =
            helloWorldService
                    .getUser(name)
                    .also { log.info { "$it" } }

    @GetMapping("/hello")
    fun hello(): String {
        return "Another Hello World!"
    }

}