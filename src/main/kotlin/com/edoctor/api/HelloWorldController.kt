package com.edoctor.api

import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import java.lang.RuntimeException

@RestController
class HelloWorldController {

    val log = logger { }

    @Autowired
    lateinit var helloWorldService: HelloWorldService

    @GetMapping("/")
    fun index(@RequestParam(value = "name") name: String) =
            helloWorldService
                    .getUser(name)
                    .also { if (name == "Bad") throw BadNameException() }
                    .also { if (name == "Good") throw HttpServerErrorException(HttpStatus.BAD_GATEWAY, "Good name!")}
                    .also { log.info { "$it" } }

    @GetMapping("/hello")
    fun hello(): String {
        return "Another Hello World!"
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Bad name!")
    class BadNameException() : RuntimeException() {
        override fun fillInStackTrace() = this
    }

}