package com.edoctor.api.controller

import com.edoctor.api.entities.network.response.UserResponseWrapper
import com.edoctor.api.files.ImageFilesStorage
import com.edoctor.api.mapper.UserMapper.toNetwork
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
class AccountController {

    private val log = logger { }

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @Autowired
    private lateinit var imageFilesStorage: ImageFilesStorage

    @GetMapping("/account")
    fun getAccount(authentication: OAuth2Authentication): ResponseEntity<UserResponseWrapper> {
        val principal = authentication.principal as User

        val patient = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
        if (patient != null) {
            return ResponseEntity.ok(toNetwork(patient))
        }

        val doctor = doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
        if (doctor != null) {
            return ResponseEntity.ok(toNetwork(doctor))
        }

        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    @PostMapping("/account")
    fun updateAccount(
            authentication: OAuth2Authentication,
            @RequestPart("userRequest", required = true) userRequestWrapper: UserResponseWrapper,
            @RequestPart("image", required = false) image: MultipartFile?
    ): ResponseEntity<UserResponseWrapper> {
        val principal = authentication.principal as User

        val patient = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
        if (patient != null) {
            val requestPatient = userRequestWrapper.patientResponse
            return if (requestPatient == null || requestPatient.email != patient.email) {
                ResponseEntity(HttpStatus.CONFLICT)
            } else {
                val newImageUuid = updateImageInStorage(patient.imageUuid, image)
                val newPatient = patient.apply {
                    fullName = requestPatient.fullName
                    city = requestPatient.city
                    dateOfBirthTimestamp = requestPatient.dateOfBirthTimestamp
                    isMale = requestPatient.isMale
                    newImageUuid?.let { imageUuid = it }
                }
                patientRepository.save(newPatient)
                ResponseEntity.ok(toNetwork(newPatient))
            }
        }

        val doctor = doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
        if (doctor != null) {
            val requestDoctor = userRequestWrapper.doctorResponse
            return if (requestDoctor == null || requestDoctor.email != doctor.email) {
                ResponseEntity(HttpStatus.CONFLICT)
            } else {
                val newImageUuid = updateImageInStorage(doctor.imageUuid, image)
                val newDoctor = doctor.apply {
                    fullName = requestDoctor.fullName
                    city = requestDoctor.city
                    dateOfBirthTimestamp = requestDoctor.dateOfBirthTimestamp
                    isMale = requestDoctor.isMale
                    newImageUuid?.let { imageUuid = it }
                }
                doctorRepository.save(newDoctor)
                ResponseEntity.ok(toNetwork(newDoctor))
            }
        }

        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    private fun updateImageInStorage(oldImageUuid: String?, image: MultipartFile?): String? {
        return if (image != null) {
            oldImageUuid?.let {
                imageFilesStorage.removeImageFile(it)
            }

            val newImageUuid = UUID.randomUUID().toString()
            imageFilesStorage.saveImageFile(newImageUuid, image.inputStream)
            newImageUuid
        } else {
            null
        }
    }


}