package com.edoctor.api.controller

import com.edoctor.api.entities.network.response.DoctorsResponse
import com.edoctor.api.entities.storage.DoctorEntity
import com.edoctor.api.mapper.UserMapper.toWrapper
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
        val doctorEntities = doctorRepository.findByFullNameIgnoreCaseContainingOrSpecializationIgnoreCaseContaining(
                textToSearch,
                textToSearch
        )

        return ResponseEntity.ok(
                DoctorsResponse(doctorEntities
                        .mapNotNull {
                            it.takeIf { it.shouldBeSearched() }
                                    ?.let { toWrapper(it) }
                                    ?.doctorModel
                        }
                        .sortedByDescending { it.isReadyForConsultation }
                )
        )
    }

    private fun DoctorEntity.shouldBeSearched(): Boolean {
        return specialization != null && fullName != null &&
                imageUuid != null && yearsOfExperience != null &&
                city != null && workExperience != null &&
                education != null && isMale != null
    }

}