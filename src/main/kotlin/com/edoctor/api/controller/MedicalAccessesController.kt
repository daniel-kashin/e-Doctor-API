package com.edoctor.api.controller

import com.edoctor.api.entities.domain.BodyParameterType.Companion.NON_CUSTOM_BODY_PARAMETER_TYPES
import com.edoctor.api.entities.domain.MedicalEventType.Companion.ALL_MEDICAL_EVENT_TYPES
import com.edoctor.api.entities.domain.MedicalRecordType
import com.edoctor.api.entities.network.model.record.MedicalAccessesForDoctorModel
import com.edoctor.api.entities.network.model.record.MedicalAccessesForPatientModel
import com.edoctor.api.entities.network.model.record.MedicalRecordTypeModel
import com.edoctor.api.entities.storage.BodyParameterEntityType
import com.edoctor.api.entities.storage.MedicalAccessEntity
import com.edoctor.api.entities.storage.PatientEntity
import com.edoctor.api.mapper.MedicalAccessMapper.toDoctorModel
import com.edoctor.api.mapper.MedicalAccessMapper.toPatientModel
import com.edoctor.api.mapper.MedicalRecordTypeMapper.toDomain
import com.edoctor.api.mapper.MedicalRecordTypeMapper.toModel
import com.edoctor.api.repositories.BodyParameterRepository
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.MedicalAccessesRepository
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.UUID.randomUUID

@RestController
class MedicalAccessesController {

    private val log = KotlinLogging.logger { }

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @Autowired
    private lateinit var medicalAccessesRepository: MedicalAccessesRepository

    @Autowired
    private lateinit var bodyParameterRepository: BodyParameterRepository

    @Autowired
    private lateinit var chatHandler: ChatHandler

    @GetMapping("/medicalAccessesForDoctor")
    fun getMedicalAccessesForDoctor(
            authentication: OAuth2Authentication,
            @RequestParam patientUuid: String?
    ): ResponseEntity<MedicalAccessesForDoctorModel> {
        val principal = authentication.principal as User

        val user = doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val accessesForDoctor = if (patientUuid == null) {
            medicalAccessesRepository.findAllByDoctorUuid(user.uuid)
                    .groupBy { it.patient }
                    .map { (patient, accesses) ->
                        toDoctorModel(patient, accesses, patient.getAllMedicalRecordTypes())
                    }
        } else {
            val patient = patientRepository.findById(patientUuid).orElse(null)
                    ?: return ResponseEntity(HttpStatus.NOT_FOUND)
            val accesses = medicalAccessesRepository.findAllByDoctorUuidAndPatientUuid(user.uuid, patientUuid)

            listOf(toDoctorModel(patient, accesses, patient.getAllMedicalRecordTypes()))
        }

        return ResponseEntity.ok(MedicalAccessesForDoctorModel(accessesForDoctor))
    }

    @GetMapping("/medicalAccessesForPatient")
    fun getMedicalAccessesForPatient(
            authentication: OAuth2Authentication,
            @RequestParam doctorUuid: String?
    ): ResponseEntity<MedicalAccessesForPatientModel> {
        val principal = authentication.principal as User

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val accessesForPatient = if (doctorUuid == null) {
            medicalAccessesRepository.findAllByPatientUuid(user.uuid)
                    .groupBy { it.doctor }
                    .map { (doctor, accesses) ->
                        toPatientModel(doctor, accesses)
                    }
        } else {
            val doctor = doctorRepository.findById(doctorUuid).orElse(null)
                    ?: return ResponseEntity(HttpStatus.NOT_FOUND)
            val accesses = medicalAccessesRepository.findAllByDoctorUuidAndPatientUuid(doctorUuid, user.uuid)

            listOf(toPatientModel(doctor, accesses))
        }

        return ResponseEntity.ok(
                MedicalAccessesForPatientModel(
                        accessesForPatient,
                        user.getAllMedicalRecordTypes()
                )
        )
    }

    @PostMapping("/medicalAccessesForPatient")
    @Transactional
    fun postMedicalAccessesForPatient(
            authentication: OAuth2Authentication,
            @RequestBody medicalAccesses: MedicalAccessesForPatientModel
    ): ResponseEntity<String> {
        val principal = authentication.principal as User

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        medicalAccesses.medicalAccesses.forEach { (doctorModel, medicalRecordTypes) ->
            val doctor = doctorRepository.findById(doctorModel.uuid).orElse(null)
                    ?: return ResponseEntity(HttpStatus.NOT_FOUND)

            medicalAccessesRepository.removeAllByDoctorUuidAndPatientUuid(doctor.uuid, user.uuid)

            medicalRecordTypes.forEach { medicalRecordType ->
                medicalAccessesRepository.save(
                        MedicalAccessEntity(
                                givenUuid = randomUUID(),
                                medicalRecordType = medicalRecordType.medicalRecordType,
                                customModelName = medicalRecordType.customModelName,
                                customModelUnit = medicalRecordType.customModelUnit,
                                patient = user,
                                doctor = doctor
                        )
                )
            }

            chatHandler.onMedicalAccessesChanged(user, doctor)
        }

        return ResponseEntity.noContent().build()
    }

    private fun PatientEntity.getAllMedicalRecordTypes(): List<MedicalRecordTypeModel> {
        return bodyParameterRepository.getDistinctTypesForPatient(uuid)
                .mapNotNull<BodyParameterEntityType, MedicalRecordType> { toDomain(it) }
                .plus(NON_CUSTOM_BODY_PARAMETER_TYPES)
                .plus(ALL_MEDICAL_EVENT_TYPES)
                .distinct()
                .filterNotNull()
                .map { toModel(it) }
    }

}