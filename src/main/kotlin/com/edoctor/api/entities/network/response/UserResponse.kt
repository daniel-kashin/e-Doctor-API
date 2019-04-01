package com.edoctor.api.entities.network.response

abstract class UserResponse {
    abstract val email: String
    abstract val fullName: String?
    abstract val city: String?
}