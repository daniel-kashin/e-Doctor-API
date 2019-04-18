package com.edoctor.api.controller

import com.edoctor.api.entities.network.request.BodyParameterTypeWrapper
import com.edoctor.api.entities.network.request.SynchronizeBodyParametersModel
import com.edoctor.api.entities.network.response.BodyParametersResponse
import com.edoctor.api.mapper.BodyParameterMapper.toEntityFromWrapper
import com.edoctor.api.mapper.BodyParameterMapper.toWrapperFromEntity
import com.edoctor.api.mapper.MedicalRecordTypeMapper
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
import com.edoctor.api.utils.MutexFactory
import com.edoctor.api.utils.currentUnixTime


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

    private val mutexFactory: MutexFactory<String> = MutexFactory()

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
                .map { toWrapperFromEntity(it) }

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
                .map { toWrapperFromEntity(it) }

        return ResponseEntity.ok(BodyParametersResponse(parameters))
    }

    @PostMapping("/synchronizeParametersForPatient")
    @Transactional
    fun synchronizeParameters(
            @RequestBody request: SynchronizeBodyParametersModel,
            authentication: OAuth2Authentication
    ): ResponseEntity<SynchronizeBodyParametersModel> {
        val principal = authentication.principal as User

        synchronized(mutexFactory.getMutex(principal.username)) {
            val currentTimestamp = currentUnixTime()

            val patient = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                    ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

            val localParameters = bodyParameterRepository
                    .getBodyParameterEntitiesByUpdateTimestampGreaterThanAndPatientUuid(
                            request.synchronizeTimestamp,
                            patient.uuid
                    )

            val remoteParameters = request.bodyParameters

            val mergedParameters = localParameters
                    .asSequence()
                    .map { it.uuid }
                    .plus(remoteParameters.map { it.uuid })
                    .distinct()
                    .filterNotNull()
                    .mapNotNull { uuid ->
                        val local = localParameters.firstOrNull { it.uuid == uuid }
                        val remote = remoteParameters.firstOrNull { it.uuid == uuid }

                        if (local == null && remote != null) {
                            remote.copy(updateTimestamp = currentTimestamp)
                        } else if (local != null && remote == null) {
                            toWrapperFromEntity(local)
                        } else if (local != null && remote != null) {
                            local.takeIf { it.updateTimestamp > remote.updateTimestamp }
                                    ?.let { toWrapperFromEntity(it) }
                                    ?: remote.copy(updateTimestamp = currentTimestamp)
                        } else {
                            null
                        }
                    }
                    .toList()

            bodyParameterRepository.saveAll(mergedParameters.map { toEntityFromWrapper(it, patient) })

            return ResponseEntity.ok(SynchronizeBodyParametersModel(mergedParameters, currentTimestamp))
        }
    }

}