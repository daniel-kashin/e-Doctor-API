package com.edoctor.api.controller

import com.edoctor.api.entities.network.response.MessagesResponse
import com.edoctor.api.entities.storage.DoctorEntity
import com.edoctor.api.entities.storage.PatientEntity
import com.edoctor.api.mapper.MessageMapper.toResponse
import com.edoctor.api.mapper.MessageMapper.wrapResponse
import com.edoctor.api.repositories.ConversationRepository
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.MessageRepository
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController {

    private val log = KotlinLogging.logger { }

    @Autowired
    private lateinit var messageRepository: MessageRepository

    @Autowired
    private lateinit var conversationRepository: ConversationRepository

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    // TODO: add getting by pages
    @GetMapping("/messages", params = ["fromTimestamp", "recipientEmail"])
    fun getMessages(
            authentication: OAuth2Authentication,
            @RequestParam("fromTimestamp") fromTimestamp: Long,
            @RequestParam("recipientEmail") recipientEmail: String
    ): ResponseEntity<MessagesResponse> {
        val principal = authentication.principal as User

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val (patientEntity, doctorEntity) = if (user is PatientEntity) {
            val doctorEntity = doctorRepository.findByEmail(recipientEmail) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
            user to doctorEntity
        } else if (user is DoctorEntity) {
            val patientEntity = patientRepository.findByEmail(recipientEmail) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
            patientEntity to user
        } else {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }

        val conversation = conversationRepository.findByPatientEmailAndDoctorEmail(patientEntity.email, doctorEntity.email)
                ?: return ResponseEntity.ok(MessagesResponse(emptyList()))

        val messages = messageRepository
                .findByTimestampGreaterThanAndConversationUuidOrderByTimestamp(
                        fromTimestamp,
                        conversation.uuid
                )
                .mapNotNull { wrapResponse(toResponse(it, patientEntity, doctorEntity)) }

        return ResponseEntity.ok(MessagesResponse(messages))
    }

}