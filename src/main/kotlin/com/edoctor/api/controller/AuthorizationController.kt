package com.edoctor.api.controller

import com.edoctor.api.entities.network.LoginRequest
import com.edoctor.api.entities.storage.Doctor
import com.edoctor.api.mapper.UserMapper
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.entities.storage.Patient
import com.edoctor.api.entities.network.UserResult
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging.logger
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

    private val log = logger { }

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @Autowired
    // TODO
    private lateinit var passwordEncoder: PasswordEncoder

    @PostMapping("/register")
    fun register(@RequestBody loginRequest: LoginRequest): ResponseEntity<UserResult> {
        if (patientRepository.existsByEmail(loginRequest.email) || doctorRepository.existsByEmail(loginRequest.email)) {
            return ResponseEntity(HttpStatus.CONFLICT)
        }

        val user: UserResult = if (loginRequest.isPatient) {
            Patient(email = loginRequest.email, password = loginRequest.password, conversations = emptySet())
                    .also { patientRepository.save(it) }
                    .let {
                        log.info { "savePatient(loginRequest = $loginRequest, patient = $it)" }
                        UserMapper.toNetwork(it)
                    }
        } else {
            Doctor(email = loginRequest.email, password = loginRequest.password, conversations = emptySet())
                    .also { doctorRepository.save(it) }
                    .let {
                        log.info { "saveDoctor(loginRequest = $loginRequest, doctor = $it)" }
                        UserMapper.toNetwork(it)
                    }
        }

        return ResponseEntity.ok(user)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<UserResult> {
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