package com.edoctor.api.controller

import com.edoctor.api.entities.network.LoginRequest
import com.edoctor.api.entities.storage.Doctor
import com.edoctor.api.mapper.UserMapper
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.entities.storage.Patient
import com.edoctor.api.entities.storage.base.User
import com.edoctor.api.entities.network.User as NetworkUser
import com.edoctor.api.repositories.PatientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.web.bind.annotation.RequestBody

@RestController
class AuthorizationController {

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    // TODO
    private lateinit var passwordEncoder: PasswordEncoder

    @PostMapping("/register")
    fun register(@RequestBody loginRequest: LoginRequest): ResponseEntity<NetworkUser> {
        if (patientRepository.findByEmail(loginRequest.email) != null
                || doctorRepository.findByEmail(loginRequest.email) != null
        ) {
            return ResponseEntity(HttpStatus.CONFLICT)
        }

        val user: NetworkUser = if (loginRequest.isPatient) {
            Patient(email = loginRequest.email, password = loginRequest.password, conversations = emptySet())
                    .also { patientRepository.save(it) }
                    .let { patient -> UserMapper.toNetwork(patient) }
        } else {
            Doctor(email = loginRequest.email, password = loginRequest.password, conversations = emptySet())
                    .also { doctorRepository.save(it) }
                    .let { UserMapper.toNetwork(it) }
        }

        return ResponseEntity.ok(user)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<NetworkUser> {
        return if (loginRequest.isPatient) {
            val patient = patientRepository.findByEmail(loginRequest.email)
                    ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

            if (passwordEncoder.matches(loginRequest.password, patient.password)) {
                ResponseEntity.ok(UserMapper.toNetwork(patient))
            } else {
                ResponseEntity(HttpStatus.CONFLICT)
            }
        } else {
            val doctor = doctorRepository.findByEmail(loginRequest.email)
                    ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

            if (passwordEncoder.matches(loginRequest.password, doctor.password)) {
                ResponseEntity.ok(UserMapper.toNetwork(doctor))
            } else {
                ResponseEntity(HttpStatus.CONFLICT)
            }
        }
    }

}