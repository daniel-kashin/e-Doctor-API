package com.edoctor.api.controller

import com.edoctor.api.files.ImageFilesStorage
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpHeaders

@RestController
class ImagesController {

    private val log = logger { }

    @Autowired
    private lateinit var imageFilesStorage: ImageFilesStorage

    @GetMapping("/images/{imageUuid}")
    fun getImage(@PathVariable imageUuid: String): ResponseEntity<ByteArray> {
        val imageFile = imageFilesStorage.getImageFile(imageUuid)

        if (!imageFile.exists()) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }

        val imageBytes = imageFile.readBytes()

        val headers = HttpHeaders().apply {
//            contentType = MediaType.IMAGE_PNG
            contentLength = imageBytes.size.toLong()
        }

        return ResponseEntity(imageBytes, headers, HttpStatus.OK)
    }

}