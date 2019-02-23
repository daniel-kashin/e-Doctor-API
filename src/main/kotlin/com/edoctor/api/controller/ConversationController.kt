package com.edoctor.api.controller

import com.edoctor.api.entities.network.ConversationsResult
import com.edoctor.api.entities.network.TextMessage
import com.edoctor.api.repositories.ConversationRepository
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

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

        log.info { "getConversations: $principal"}

        val (isPatient, uuid) = let {
            val patient = patientRepository.findByEmail(principal.username)
            if (patient != null) {
                true to patient.uuid
            } else {
                val doctor = doctorRepository.findByEmail(principal.username)
                        ?: return ResponseEntity(HttpStatus.CONFLICT)
                false to doctor.uuid
            }
        }

        val conversations = if (isPatient) {
            conversationRepository.findByPatientUuid(uuid).map {
                val lastMessage = it.messages.first()
                TextMessage(lastMessage.uuid, uuid, it.doctor.uuid, lastMessage.timestamp, lastMessage.text)
            }
        } else {
            conversationRepository.findByDoctorUuid(uuid).map {
                val lastMessage = it.messages.first()
                TextMessage(lastMessage.uuid, uuid, it.patient.uuid, lastMessage.timestamp, lastMessage.text)
            }
        }

        return ResponseEntity.ok(ConversationsResult(conversations))
    }

}