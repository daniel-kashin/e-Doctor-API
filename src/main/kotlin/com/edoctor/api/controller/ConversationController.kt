package com.edoctor.api.controller

import com.edoctor.api.entities.network.ConversationsResult
import com.edoctor.api.entities.network.TextMessage
import com.edoctor.api.entities.storage.Doctor
import com.edoctor.api.entities.storage.Patient
import com.edoctor.api.repositories.ConversationRepository
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.IllegalStateException

@RestController
class ConversationController {

    private val log = logger { }

    @Autowired
    private lateinit var conversationRepository: ConversationRepository

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    // TODO: add getting by pages
    @GetMapping("/conversations")
    fun getConversations(authentication: OAuth2Authentication): ResponseEntity<ConversationsResult> {
        val principal = authentication.principal as User

        log.info { "getConversations: $principal" }

        val user = let {
            val patient = patientRepository.findByEmail(principal.username)
            if (patient != null) {
                log.info { "got patient: $patient" }
                patient
            } else {
                val doctor = doctorRepository.findByEmail(principal.username)
                        ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

                log.info { "got doctor: $doctor" }

                doctor
            }
        }

        val conversations = when (user) {
            is Patient -> user.conversations.map { conversation ->
                val message = conversation.messages.first()
                log.info { "got message ${message.text} from ${conversation.doctor.email}" }
                val senderEmail = if (message.isFromPatient) user.email else conversation.doctor.email
                val recipientEmail = if (message.isFromPatient) conversation.doctor.email else user.email
                TextMessage(message.uuid, senderEmail, recipientEmail, message.timestamp, message.text)
                        .also { log.info { "created $it" } }
            }
            is Doctor -> user.conversations.map { conversation ->
                val message = conversation.messages.first()
                log.info { "got message ${message.text} from ${conversation.patient.email}" }
                val senderEmail = if (message.isFromPatient) conversation.patient.email else user.email
                val recipientEmail = if (message.isFromPatient) user.email else conversation.patient.email
                TextMessage(message.uuid, senderEmail, recipientEmail, message.timestamp, message.text)
                        .also { log.info { "created $it" } }
            }
            else -> throw IllegalStateException()
        }

        return ResponseEntity.ok(ConversationsResult(conversations.sortedByDescending { it.sendingTimestamp }))
    }

}