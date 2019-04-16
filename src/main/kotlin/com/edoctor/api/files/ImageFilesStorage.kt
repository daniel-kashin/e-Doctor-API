package com.edoctor.api.files

import mu.KotlinLogging.logger
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@Component
class ImageFilesStorage {

    private val log = logger {}

    companion object {
        private const val IMAGES_DIRECTORY = "/Users/Shared/images"
    }

    private val imagesDirectory = File(IMAGES_DIRECTORY).also { it.mkdirs() }

    fun saveImageFile(imageUuid: String, inputStream: InputStream) {
        log.info { "saveImageFile($imageUuid)" }
        File(imagesDirectory, imageUuid).also { file ->
            inputStream.use {
                Files.copy(it, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    fun getImageFile(imageUuid: String): File {
        log.info { "getImageFile($imageUuid)" }
        return File(imagesDirectory, imageUuid)
    }

    fun removeImageFile(imageUuid: String) {
        log.info { "removeImageFile($imageUuid)" }
        File(imagesDirectory, imageUuid)
                .takeIf { it.exists() }
                ?.delete()
    }

}