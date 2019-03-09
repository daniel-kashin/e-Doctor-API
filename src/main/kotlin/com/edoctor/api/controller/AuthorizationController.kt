package com.edoctor.api.controller

import com.edoctor.api.entities.network.request.LoginRequest
import com.edoctor.api.entities.storage.DoctorEntity
import com.edoctor.api.mapper.UserMapper
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.entities.storage.PatientEntity
import com.edoctor.api.entities.network.response.UserResponse
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID.randomUUID

@RestController
class AuthorizationController {

    private val log = logger { }

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @Autowired
    // TODO
    private lateinit var passwordEncoder: PasswordEncoder

    @PostMapping("/register")
    fun register(@RequestBody loginRequest: LoginRequest): ResponseEntity<UserResponse> {
        if (patientRepository.existsByEmail(loginRequest.email) || doctorRepository.existsByEmail(loginRequest.email)) {
            return ResponseEntity(HttpStatus.CONFLICT)
        }

        val user: UserResponse = if (loginRequest.isPatient) {
            PatientEntity(givenUuid = randomUUID(), email = loginRequest.email, password = loginRequest.password, conversations = mutableSetOf())
                    .let {
                        log.info { "savePatient(loginRequest = $loginRequest, patient = $it)" }
                        patientRepository.save(it)
                        UserMapper.toNetwork(it)
                    }
        } else {
            DoctorEntity(givenUuid = randomUUID(), email = loginRequest.email, password = loginRequest.password, conversations = mutableSetOf())
                    .let {
                        log.info { "saveDoctor(loginRequest = $loginRequest, doctor = $it)" }
                        doctorRepository.save(it)
                        UserMapper.toNetwork(it)
                    }
        }

        return ResponseEntity.ok(user)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<UserResponse> {
        return if (loginRequest.isPatient) {
            val patient = patientRepository.findByEmail(loginRequest.email)
                    ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

            log.info { "getPatient(loginRequest = $loginRequest, patient = $patient)" }

            if (passwordEncoder.matches(loginRequest.password, patient.password)) {
                ResponseEntity.ok(UserMapper.toNetwork(patient))
            } else {
                ResponseEntity(HttpStatus.CONFLICT)
            }
        } else {
            val doctor = doctorRepository.findByEmail(loginRequest.email)
                    ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

            log.info { "getDoctor(loginRequest = $loginRequest, doctor = $doctor)" }

            if (passwordEncoder.matches(loginRequest.password, doctor.password)) {
                ResponseEntity.ok(UserMapper.toNetwork(doctor))
            } else {
                ResponseEntity(HttpStatus.CONFLICT)
            }
        }
    }

}