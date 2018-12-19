package com.edoctor.api.entities

abstract class User {
    abstract val uuid: String
    abstract val email: String
    abstract val password: String
    abstract val name: String?
    abstract val lastName: String?
    abstract val patronymic: String?
    abstract val currentCity: String?
    abstract val gender: Gender?
    abstract val birthdayTimestamp: Long?
}

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}


data class Patient(
        override val uuid: String,
        override val email: String,
        override val password: String,
        override val name: String?,
        override val lastName: String?,
        override val patronymic: String?,
        override val currentCity: String?,
        override val gender: Gender?,
        override val birthdayTimestamp: Long?
) : User()

data class Doctor(
        override val uuid: String,
        override val email: String,
        override val password: String,
        override val name: String?,
        override val lastName: String?,
        override val patronymic: String?,
        override val currentCity: String?,
        override val gender: Gender?,
        override val birthdayTimestamp: Long?
) : User()