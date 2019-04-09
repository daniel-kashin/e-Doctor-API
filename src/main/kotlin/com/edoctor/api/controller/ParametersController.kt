package com.edoctor.api.controller

import com.edoctor.api.entities.network.model.record.BodyParameterWrapper
import com.edoctor.api.entities.network.request.BodyParameterTypeWrapper
import com.edoctor.api.entities.network.response.BodyParametersResponse
import com.edoctor.api.mapper.BodyParameterMapper.toEntity
import com.edoctor.api.mapper.BodyParameterMapper.toNetwork
import com.edoctor.api.repositories.BodyParameterRepository
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ParametersController {

    private val log = KotlinLogging.logger { }

    @Autowired
    private lateinit var bodyParameterRepository: BodyParameterRepository

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @GetMapping("/latestParameters")
    @Transactional
    fun getLatestParametersOfEachType(
            authentication: OAuth2Authentication
    ): ResponseEntity<BodyParametersResponse> {
        val principal = authentication.principal as User

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val distinctTypes = bodyParameterRepository.getDistinctTypes()

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

    @PostMapping("/parameters")
    @Transactional
    fun getParameters(
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

    @PostMapping("/addOrEditParameter")
    @Transactional
    fun addOrEditParameter(
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

    @PostMapping("/deleteParameter")
    @Transactional
    fun deleteParameter(
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