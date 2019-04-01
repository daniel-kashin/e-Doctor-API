package com.edoctor.api.controller

import com.edoctor.api.entities.network.response.UserResponseWrapper
import com.edoctor.api.entities.storage.PatientEntity
import com.edoctor.api.mapper.UserMapper.toNetwork
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController {

    private val log = KotlinLogging.logger { }

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @GetMapping("/account")
    fun getAccount(authentication: OAuth2Authentication): ResponseEntity<UserResponseWrapper> {
        val principal = authentication.principal as User

        val patient = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
        if (patient != null) {
            return ResponseEntity.ok(toNetwork(patient))
        }

        val doctor = doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
        if (doctor != null) {
            return ResponseEntity.ok(toNetwork(doctor))
        }

        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    @PostMapping("/account")
    fun updateAccount(
            authentication: OAuth2Authentication,
            @RequestBody userRequesWrapper: UserResponseWrapper
    ): ResponseEntity<UserResponseWrapper> {
        val principal = authentication.principal as User

        val patient = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
        if (patient != null) {
            val requestPatient = userRequesWrapper.patientResponse
            return if (requestPatient == null || requestPatient.email != patient.email) {
                ResponseEntity(HttpStatus.CONFLICT)
            } else {
                val newPatient = patient.run {
                    PatientEntity(originalUuid, requestPatient.fullName, requestPatient.city, email, password, conversations)
                }
                ResponseEntity.ok(toNetwork(newPatient))
            }
        }

        val doctor = doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
        if (doctor != null) {
            val requestDoctor = userRequesWrapper.doctorResponse
            return if (requestDoctor == null || requestDoctor.email != doctor.email) {
                ResponseEntity(HttpStatus.CONFLICT)
            } else {
                val newPatient = doctor.run {
                    PatientEntity(originalUuid, requestDoctor.fullName, requestDoctor.city, email, password, conversations)
                }
                ResponseEntity.ok(toNetwork(newPatient))
            }
        }

        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }


}