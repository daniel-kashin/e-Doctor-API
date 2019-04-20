package com.edoctor.api.controller

import com.edoctor.api.entities.network.model.user.UserModelWrapper
import com.edoctor.api.files.ImageFilesStorage
import com.edoctor.api.mapper.UserMapper.toWrapper
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
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
    fun getAccount(authentication: OAuth2Authentication): ResponseEntity<UserModelWrapper> {
        val principal = authentication.principal as User

        val patient = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
        if (patient != null) {
            return ResponseEntity.ok(toWrapper(patient))
        }

        val doctor = doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
        if (doctor != null) {
            return ResponseEntity.ok(toWrapper(doctor))
        }

        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    @PostMapping("/account")
    fun updateAccount(
            authentication: OAuth2Authentication,
            @RequestPart("userRequest", required = true) userRequestWrapper: UserModelWrapper,
            @RequestPart("image", required = false) image: MultipartFile?
    ): ResponseEntity<UserModelWrapper> {
        val principal = authentication.principal as User

        val patient = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
        if (patient != null) {
            val requestPatient = userRequestWrapper.patientModel
            return if (requestPatient == null || requestPatient.uuid != patient.uuid) {
                ResponseEntity(HttpStatus.CONFLICT)
            } else {
                val newImageUuid = updateImageInStorage(patient.imageUuid, image)
                val newPatient = patient.apply {
                    fullName = requestPatient.fullName
                    city = requestPatient.city
                    dateOfBirthTimestamp = requestPatient.dateOfBirthTimestamp
                    isMale = requestPatient.isMale
                    bloodGroup = requestPatient.bloodGroup
                    newImageUuid?.let { imageUuid = it }
                }
                patientRepository.save(newPatient)
                ResponseEntity.ok(toWrapper(newPatient))
            }
        }

        val doctor = doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
        if (doctor != null) {
            val requestDoctor = userRequestWrapper.doctorModel
            return if (requestDoctor == null || requestDoctor.uuid != doctor.uuid) {
                ResponseEntity(HttpStatus.CONFLICT)
            } else {
                val newImageUuid = updateImageInStorage(doctor.imageUuid, image)
                val newDoctor = doctor.apply {
                    fullName = requestDoctor.fullName
                    city = requestDoctor.city
                    dateOfBirthTimestamp = requestDoctor.dateOfBirthTimestamp
                    isMale = requestDoctor.isMale
                    yearsOfExperience = requestDoctor.yearsOfExperience
                    category = requestDoctor.category
                    specialization = requestDoctor.specialization
                    clinicalInterests = requestDoctor.clinicalInterests
                    workExperience = requestDoctor.workExperience
                    education = requestDoctor.education
                    trainings = requestDoctor.trainings
                    newImageUuid?.let { imageUuid = it }
                }
                doctorRepository.save(newDoctor)
                ResponseEntity.ok(toWrapper(newDoctor))
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