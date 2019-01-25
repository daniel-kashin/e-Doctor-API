package com.edoctor.api.entities.network

data class LoginRequest(val email: String, val password: String, val isPatient: Boolean)