package com.edoctor.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {

    @Autowired
    lateinit var helloWorldService: HelloWorldService

    @GetMapping("/")
    fun index(@RequestParam(value = "name") name: String) = helloWorldService.getUser(name)

    @GetMapping("/hello")
    fun hello(): String {
        return "Another Hello World!"
    }

}