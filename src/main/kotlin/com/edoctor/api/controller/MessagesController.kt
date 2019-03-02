package com.edoctor.api.controller

import com.edoctor.api.entities.network.MessagesResult
import com.edoctor.api.entities.storage.Doctor
import com.edoctor.api.entities.storage.Patient
import com.edoctor.api.mapper.MessageMapper
import com.edoctor.api.repositories.ConversationRepository
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.MessagesRepository
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
class MessagesController {

    private val log = KotlinLogging.logger { }

    @Autowired
    private lateinit var messagesRepository: MessagesRepository

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
    ): ResponseEntity<MessagesResult> {
        val principal = authentication.principal as User

        log.info { "getMessages: $principal" }

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val patientEmail = user.takeIf { it is Patient }?.email ?: recipientEmail
        val doctorEmail = user.takeIf { it is Doctor }?.email ?: recipientEmail

        val conversation = conversationRepository.findByPatientEmailAndDoctorEmail(patientEmail, doctorEmail)
                ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val messages = messagesRepository
                .findByTimestampGreaterThanAndConversationUuidOrderByTimestamp(
                        fromTimestamp,
                        conversation.uuid
                )
                .map { MessageMapper.toNetwork(it, patientEmail, doctorEmail) }
                .also {
                    it.forEach { message ->
                        log.info {
                            "getMessage(givenUuid=${message.uuid}, text=${message.text}, timestamp=${message.sendingTimestamp}"
                        }
                    }
                }

        return ResponseEntity.ok(MessagesResult(messages))
    }

}