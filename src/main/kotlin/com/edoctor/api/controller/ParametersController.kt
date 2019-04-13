package com.edoctor.api.controller

import com.edoctor.api.entities.network.model.record.BodyParameterWrapper
import com.edoctor.api.entities.network.request.BodyParameterTypeWrapper
import com.edoctor.api.entities.network.response.BodyParametersResponse
import com.edoctor.api.mapper.BodyParameterMapper.toEntity
import com.edoctor.api.mapper.BodyParameterMapper.toNetwork
import com.edoctor.api.mapper.MedicalRecordTypeMapper
import com.edoctor.api.mapper.MedicalRecordTypeMapper.toDomain
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

@RestController
class ParametersController {

    private val log = KotlinLogging.logger { }

    @Autowired
    private lateinit var bodyParameterRepository: BodyParameterRepository

    @Autowired
    private lateinit var medicalAccessesRepository: MedicalAccessesRepository

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @GetMapping("/latestParametersForPatient")
    @Transactional
    fun getLatestParametersOfEachTypeForPatient(
            authentication: OAuth2Authentication
    ): ResponseEntity<BodyParametersResponse> {
        val principal = authentication.principal as User

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val distinctTypes = bodyParameterRepository.getDistinctTypesForPatient(user.uuid)

        val parameters = distinctTypes
                .mapNotNull {
                    bodyParameterRepository
                            .findTopByTypeAndCustomModelNameAndCustomModelUnitAndPatientUuidOrderByMeasurementTimestampDesc(
                                    it.type,
                                    it.customModelName,
                                    it.customModelUnit,
                                    user.uuid
                            )
                }
                .map { toNetwork(it) }

        return ResponseEntity.ok(BodyParametersResponse(parameters))
    }

    @GetMapping("/latestParametersForDoctor")
    @Transactional
    fun getLatestParametersOfEachTypeForDoctor(
            authentication: OAuth2Authentication,
            @RequestParam patientUuid: String
    ): ResponseEntity<BodyParametersResponse> {
        val principal = authentication.principal as User

        val doctor = doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val patient = patientRepository.findById(patientUuid).orElse(null)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val domainAccesses = medicalAccessesRepository.findAllByDoctorUuidAndPatientUuid(doctor.uuid, patient.uuid)
                .map { MedicalRecordTypeMapper.toDomain(it) }

        val distinctTypes = bodyParameterRepository.getDistinctTypesForPatient(patient.uuid)
                .filter { MedicalRecordTypeMapper.toDomain(it) in domainAccesses }

        val parameters = distinctTypes
                .mapNotNull {
                    bodyParameterRepository
                            .findTopByTypeAndCustomModelNameAndCustomModelUnitAndPatientUuidOrderByMeasurementTimestampDesc(
                                    it.type,
                                    it.customModelName,
                                    it.customModelUnit,
                                    patient.uuid
                            )
                }
                .map { toNetwork(it) }

        return ResponseEntity.ok(BodyParametersResponse(parameters))
    }

    @PostMapping("/parametersForPatient")
    @Transactional
    fun getParametersForPatient(
            @RequestBody type: BodyParameterTypeWrapper,
            authentication: OAuth2Authentication
    ): ResponseEntity<BodyParametersResponse> {
        val principal = authentication.principal as User

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val parameters = bodyParameterRepository
                .findAllByTypeAndCustomModelNameAndCustomModelUnitAndPatientUuid(
                        type.type,
                        type.customModelName,
                        type.customModelUnit,
                        user.uuid
                )
                .map { toNetwork(it) }

        return ResponseEntity.ok(BodyParametersResponse(parameters))
    }

    @PostMapping("/parametersForDoctor")
    @Transactional
    fun getParametersForDoctor(
            @RequestBody type: BodyParameterTypeWrapper,
            @RequestParam patientUuid: String,
            authentication: OAuth2Authentication
    ): ResponseEntity<BodyParametersResponse> {
        val principal = authentication.principal as User

        val doctor = doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val patient = patientRepository.findById(patientUuid).orElse(null)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val domainAccesses = medicalAccessesRepository.findAllByDoctorUuidAndPatientUuid(doctor.uuid, patient.uuid)
                .map { MedicalRecordTypeMapper.toDomain(it) }

        val domainType = MedicalRecordTypeMapper.toDomain(type)
        if (domainType !in domainAccesses) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }

        val parameters = bodyParameterRepository
                .findAllByTypeAndCustomModelNameAndCustomModelUnitAndPatientUuid(
                        type.type,
                        type.customModelName,
                        type.customModelUnit,
                        patient.uuid
                )
                .map { toNetwork(it) }

        return ResponseEntity.ok(BodyParametersResponse(parameters))
    }

    @PostMapping("/addOrEditParameterForPatient")
    @Transactional
    fun addOrEditParameterForPatient(
            @RequestBody parameter: BodyParameterWrapper,
            authentication: OAuth2Authentication
    ): ResponseEntity<BodyParameterWrapper> {
        val principal = authentication.principal as User

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val existing = bodyParameterRepository.findByUuidAndPatientUuid(parameter.uuid, user.uuid)

        if (existing == null) {
            bodyParameterRepository.save(toEntity(parameter, user))
        } else {
            bodyParameterRepository.save(
                    existing.apply {
                        measurementTimestamp = parameter.measurementTimestamp
                        firstValue = parameter.firstValue
                        secondValue = parameter.secondValue
                    }
            )
        }

        return ResponseEntity.ok(parameter)
    }

    @PostMapping("/deleteParameterForPatient")
    @Transactional
    fun deleteParameterForPatient(
            @RequestBody parameter: BodyParameterWrapper,
            authentication: OAuth2Authentication
    ): ResponseEntity<String> {
        val principal = authentication.principal as User

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        bodyParameterRepository.deleteByUuidAndPatientUuid(parameter.uuid, user.uuid)

        return ResponseEntity.noContent().build()
    }

}