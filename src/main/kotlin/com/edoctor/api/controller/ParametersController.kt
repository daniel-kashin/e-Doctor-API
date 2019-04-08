package com.edoctor.api.controller

import com.edoctor.api.entities.network.model.record.BodyParameterWrapper
import com.edoctor.api.entities.network.request.BodyParameterTypeWrapper
import com.edoctor.api.entities.network.response.BodyParametersResponse
import com.edoctor.api.mapper.BodyParameterMapper.toEntity
import com.edoctor.api.mapper.BodyParameterMapper.toNetwork
import com.edoctor.api.repositories.BodyParameterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ParametersController {

    @Autowired
    private lateinit var bodyParameterRepository: BodyParameterRepository

    @GetMapping("/latestParameters")
    fun getLatestParametersOfEachType(): ResponseEntity<BodyParametersResponse> {
        val distinctTypes = bodyParameterRepository.getDistinctTypes()

        val parameters = distinctTypes
                .mapNotNull {
                    bodyParameterRepository
                            .findTopByTypeAndCustomModelNameAndCustomModelUnitOrderByMeasurementTimestampDesc(
                                    it.type,
                                    it.customModelName,
                                    it.customModelUnit
                            )
                }
                .map { toNetwork(it) }

        return ResponseEntity.ok(BodyParametersResponse(parameters))
    }

    @PostMapping("/parameters")
    fun getParameters(
            @RequestBody type: BodyParameterTypeWrapper
    ): ResponseEntity<BodyParametersResponse> {
        val parameters = bodyParameterRepository
                .findAllByTypeAndCustomModelNameAndCustomModelUnit(
                        type.type,
                        type.customModelName,
                        type.customModelUnit
                )
                .map { toNetwork(it) }

        return ResponseEntity.ok(BodyParametersResponse(parameters))
    }

    @PostMapping("/addOrEditParameter")
    fun addOrEditParameter(
            @RequestBody parameter: BodyParameterWrapper
    ): ResponseEntity<BodyParameterWrapper> {
        val existing = bodyParameterRepository.findById(parameter.uuid).orElse(null)

        if (existing == null) {
            bodyParameterRepository.save(toEntity(parameter))
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
    fun deleteParameter(
            @RequestBody parameter: BodyParameterWrapper
    ): ResponseEntity<String> {
        bodyParameterRepository.deleteById(parameter.uuid)

        return ResponseEntity.noContent().build()
    }

}