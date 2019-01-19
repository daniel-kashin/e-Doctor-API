package com.edoctor.api.exception

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpServerErrorException

class NoLogHttpServerErrorExceprion(statusCode: HttpStatus, text: String) : HttpServerErrorException(statusCode, text) {
    override fun fillInStackTrace() = this
}