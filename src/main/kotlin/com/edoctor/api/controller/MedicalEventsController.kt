package com.edoctor.api.controller

import com.edoctor.api.entities.network.model.record.MedicalEventWrapper
import com.edoctor.api.entities.network.response.MedicalEventsResponse
import com.edoctor.api.mapper.MedicalEventMapper
import com.edoctor.api.repositories.MedicalEventRepository
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
class MedicalEventsController {

    private val log = KotlinLogging.logger { }

    @Autowired
    private lateinit var medicalEventRepository: MedicalEventRepository

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @GetMapping("/medicalEvents")
    fun getEvents(
            authentication: OAuth2Authentication
    ): ResponseEntity<MedicalEventsResponse> {
        val principal = authentication.principal as User

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val events = user.medicalEvents.map { MedicalEventMapper.toNetwork(it) }

        return ResponseEntity.ok(MedicalEventsResponse(events))
    }

    @PostMapping("/addOrEditMedicalEvent")
    fun addOrEditMedicalEvent(
            @RequestBody event: MedicalEventWrapper,
            authentication: OAuth2Authentication
    ): ResponseEntity<MedicalEventWrapper> {
        val principal = authentication.principal as User

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val existing = medicalEventRepository.findByUuidAndPatientUuid(event.uuid, user.uuid)

        if (existing == null) {
            medicalEventRepository.save(MedicalEventMapper.toEntity(event, user))
        } else {
            medicalEventRepository.save(
                    existing.apply {
                        timestamp = event.timestamp
                        endTimestamp = event.endTimestamp
                        name = event.name
                        clinic = event.clinic
                        doctorName = event.doctorName
                        doctorSpecialization = event.doctorSpecialization
                        symptoms = event.symptoms
                        diagnosis = event.diagnosis
                        recipe = event.recipe
                        comment = event.comment
                    }
            )
        }

        return ResponseEntity.ok(event)
    }

    @PostMapping("/deleteMedicalEvent")
    fun deleteParameter(
            @RequestBody event: MedicalEventWrapper,
            authentication: OAuth2Authentication
    ): ResponseEntity<String> {
        val principal = authentication.principal as User

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        medicalEventRepository.deleteByUuidAndPatientUuid(event.uuid, user.uuid)

        return ResponseEntity.noContent().build()
    }

}