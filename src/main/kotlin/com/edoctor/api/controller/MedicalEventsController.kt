package com.edoctor.api.controller

import com.edoctor.api.entities.network.model.record.MedicalEventWrapper
import com.edoctor.api.entities.network.response.MedicalEventsResponse
import com.edoctor.api.entities.storage.MedicalEventEntityType
import com.edoctor.api.mapper.MedicalEventMapper
import com.edoctor.api.mapper.MedicalRecordTypeMapper
import com.edoctor.api.mapper.MedicalRecordTypeMapper.toDomain
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.MedicalAccessesRepository
import com.edoctor.api.repositories.MedicalEventRepository
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
class MedicalEventsController {

    private val log = KotlinLogging.logger { }

    @Autowired
    private lateinit var medicalEventRepository: MedicalEventRepository

    @Autowired
    private lateinit var medicalAccessesRepository: MedicalAccessesRepository

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @GetMapping("/medicalEventsForPatient")
    @Transactional
    fun getEventsForPatient(
            authentication: OAuth2Authentication
    ): ResponseEntity<MedicalEventsResponse> {
        val principal = authentication.principal as User

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val events = user.medicalEvents
                .filter { it.doctorCreator == null || it.isAddedFromDoctor }
                .map { MedicalEventMapper.toNetwork(it) }

        return ResponseEntity.ok(MedicalEventsResponse(events))
    }

    @GetMapping("/medicalEventsForDoctor")
    @Transactional
    fun getEventsForDoctor(
            authentication: OAuth2Authentication,
            @RequestParam patientUuid: String
    ): ResponseEntity<MedicalEventsResponse> {
        val principal = authentication.principal as User

        val doctor = doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val patient = patientRepository.findById(patientUuid).orElse(null)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val domainAccesses = medicalAccessesRepository.findAllByDoctorUuidAndPatientUuid(doctor.uuid, patientUuid)
                .map { MedicalRecordTypeMapper.toDomain(it) }

        val events = patient.medicalEvents
                .filter { toDomain(MedicalEventEntityType(it.type)) in domainAccesses }
                .map { MedicalEventMapper.toNetwork(it) }

        return ResponseEntity.ok(MedicalEventsResponse(events))
    }

    @PostMapping("/addOrEditMedicalEventForPatient")
    @Transactional
    fun addOrEditMedicalEventForPatient(
            @RequestBody event: MedicalEventWrapper,
            authentication: OAuth2Authentication
    ): ResponseEntity<MedicalEventWrapper> {
        val principal = authentication.principal as User

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val existing = medicalEventRepository.findByUuidAndPatientUuid(event.uuid, user.uuid)

        if (existing == null) {
            medicalEventRepository.save(MedicalEventMapper.toEntity(event, user, null))
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

    @PostMapping("/deleteMedicalEventForPatient")
    @Transactional
    fun deleteParameterForPatient(
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