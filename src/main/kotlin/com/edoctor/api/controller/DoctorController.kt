package com.edoctor.api.controller

import com.edoctor.api.entities.network.response.DoctorsResponse
import com.edoctor.api.mapper.UserMapper.toNetwork
import com.edoctor.api.repositories.DoctorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class DoctorController {

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @GetMapping("/doctors")
    fun getDoctors(
            @RequestParam("textToSearch") textToSearch: String
    ): ResponseEntity<DoctorsResponse> {
        val doctorEntities = doctorRepository.findByEmailContainingIgnoreCase(textToSearch)

        return ResponseEntity.ok(
                DoctorsResponse(doctorEntities.mapNotNull {
                    toNetwork(it).doctorResponse
                })
        )
    }

}