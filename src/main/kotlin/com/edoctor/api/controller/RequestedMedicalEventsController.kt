package com.edoctor.api.controller

import com.edoctor.api.entities.network.model.record.MedicalEventWrapper
import com.edoctor.api.entities.network.response.MedicalEventsResponse
import com.edoctor.api.mapper.MedicalEventMapper
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.MedicalEventRepository
import com.edoctor.api.repositories.PatientRepository
import com.edoctor.api.utils.currentUnixTime
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
class RequestedMedicalEventsController {

    private val log = KotlinLogging.logger { }

    @Autowired
    private lateinit var medicalEventRepository: MedicalEventRepository

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @Autowired
    private lateinit var chatHandler: ChatHandler

    @GetMapping("/requestedMedicalEventsForDoctor")
    @Transactional
    fun getRequestedEventsForDoctor(
            authentication: OAuth2Authentication,
            @RequestParam("patientUuid") patientUuid: String
    ): ResponseEntity<MedicalEventsResponse> {
        val principal = authentication.principal as User

        val doctor = doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val patient = patientRepository.findById(patientUuid).orElse(null)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val events = patient.medicalEvents
                .filter { it.doctorCreator?.uuid == doctor.uuid }
                .map { MedicalEventMapper.toWrapperFromEntity(it) }

        return ResponseEntity.ok(MedicalEventsResponse(events))
    }

    @PostMapping("/addMedicalEventForDoctor")
    @Transactional
    fun addMedicalEventForDoctor(
            @RequestBody event: MedicalEventWrapper,
            @RequestParam("patientUuid") patientUuid: String,
            authentication: OAuth2Authentication
    ): ResponseEntity<MedicalEventWrapper> {
        val principal = authentication.principal as User

        val doctor = doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val patient = patientRepository.findById(patientUuid).orElse(null)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val existing = medicalEventRepository.findByUuidAndPatientUuid(event.uuid, patient.uuid)

        if (existing != null) {
            return ResponseEntity(HttpStatus.CONFLICT)
        }

        val entityToSave = MedicalEventMapper.toEntityFromWrapper(event, patient, doctor, currentUnixTime())

        medicalEventRepository.save(entityToSave)

        chatHandler.onMedicalRecordRequest(patient, doctor)

        return ResponseEntity.ok(MedicalEventMapper.toWrapperFromEntity(entityToSave))
    }

}