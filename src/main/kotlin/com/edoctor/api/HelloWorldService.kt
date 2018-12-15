package com.edoctor.api

import org.springframework.stereotype.Service

@Service
class HelloWorldService {

    fun getUser(name: String) = User(name)

    data class User(val name: String)

}