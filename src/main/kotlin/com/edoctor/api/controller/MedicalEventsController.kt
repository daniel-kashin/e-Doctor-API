package com.edoctor.api.controller

import com.edoctor.api.entities.network.model.record.MedicalEventWrapper
import com.edoctor.api.entities.network.response.MedicalEventsResponse
import com.edoctor.api.mapper.MedicalEventMapper
import com.edoctor.api.repositories.MedicalEventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MedicalEventsController {

    @Autowired
    private lateinit var medicalEventRepository: MedicalEventRepository

    @GetMapping("/medicalEvents")
    fun getEvents(): ResponseEntity<MedicalEventsResponse> {
        val events = medicalEventRepository.findAll()
                .map { MedicalEventMapper.toNetwork(it) }

        return ResponseEntity.ok(MedicalEventsResponse(events))
    }

    @PostMapping("/addOrEditMedicalEvent")
    fun addOrEditMedicalEvent(
            @RequestBody event: MedicalEventWrapper
    ): ResponseEntity<MedicalEventWrapper> {
        val existing = medicalEventRepository.findById(event.uuid).orElse(null)

        if (existing == null) {
            medicalEventRepository.save(MedicalEventMapper.toEntity(event))
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
            @RequestBody event: MedicalEventWrapper
    ): ResponseEntity<String> {
        medicalEventRepository.deleteById(event.uuid)

        return ResponseEntity.noContent().build()
    }

}