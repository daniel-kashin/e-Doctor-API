package com.edoctor.api.controller

import com.edoctor.api.configuration.socket.WebSocketConfiguration
import com.edoctor.api.configuration.socket.WebSocketPrincipal
import com.edoctor.api.files.ImageFilesStorage
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpHeaders
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
class ImagesController {

    companion object {
        fun toRelativeImageUrl(imageUuid: String): String = "/images/$imageUuid"
    }

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @Autowired
    private lateinit var imageFilesStorage: ImageFilesStorage

    @Autowired
    private lateinit var chatHandler: ChatHandler

    @GetMapping("/images/{imageUuid}")
    fun getImage(@PathVariable imageUuid: String): ResponseEntity<ByteArray> {
        val imageFile = imageFilesStorage.getImageFile(imageUuid)

        if (!imageFile.exists()) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }

        val imageBytes = imageFile.readBytes()

        val headers = HttpHeaders().apply {
            contentLength = imageBytes.size.toLong()
        }

        return ResponseEntity(imageBytes, headers, HttpStatus.OK)
    }

    @PostMapping("/images/send")
    fun sendImageToUser(
            authentication: OAuth2Authentication,
            @RequestPart("image", required = false) image: MultipartFile,
            @RequestParam recipientEmail: String
    ): ResponseEntity<String> {
        val principal = authentication.principal as User

        val webSocketPrincipal: WebSocketPrincipal = let {
            val doctor = doctorRepository.findByEmail(principal.username)
            if (doctor != null) {
                WebSocketPrincipal(principal.username, recipientEmail, false)
            } else {
                val patient = patientRepository.findByEmail(principal.username)
                if (patient != null) {
                    WebSocketPrincipal(principal.username, recipientEmail, true)
                } else {
                    null
                }
            }
        } ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val newImageUuid = UUID.randomUUID().toString()

        imageFilesStorage.saveImageFile(newImageUuid, image.inputStream)

        chatHandler.onImageUploaded(webSocketPrincipal, newImageUuid)

        return ResponseEntity.noContent().build()
    }

}