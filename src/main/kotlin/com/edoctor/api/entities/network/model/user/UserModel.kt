package com.edoctor.api.entities.network.model.user

abstract class UserModel {
    abstract val email: String
    abstract val fullName: String?
    abstract val city: String?
    abstract val dateOfBirthTimestamp: Long?
    abstract val isMale: Boolean?
    abstract val relativeImageUrl: String?
}