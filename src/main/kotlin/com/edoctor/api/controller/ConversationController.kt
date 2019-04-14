package com.edoctor.api.controller

import com.edoctor.api.entities.network.response.ConversationsResponse
import com.edoctor.api.entities.storage.DoctorEntity
import com.edoctor.api.entities.storage.PatientEntity
import com.edoctor.api.mapper.MessageMapper.toResponse
import com.edoctor.api.mapper.MessageMapper.wrapResponse
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
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    // TODO: add getting by pages
    @GetMapping("/conversations")
    fun getConversations(authentication: OAuth2Authentication): ResponseEntity<ConversationsResponse> {
        val principal = authentication.principal as User

        log.info { "getConversations: $principal" }

        val user = patientRepository.findByEmail(principal.username)?.also { log.info { "got patient: $it" } }
                ?: doctorRepository.findByEmail(principal.username)?.also { log.info { "got doctor: $it" } }
                ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        val conversations = when (user) {
            is PatientEntity -> user.conversations
            is DoctorEntity -> user.conversations
            else -> throw IllegalStateException()
        }

        val sortedConversations = conversations
                .mapNotNull { conversation ->
                    conversation.messages.maxBy { it.timestamp }?.let {
                        toResponse(it, conversation.patient, conversation.doctor)
                    }
                }
                .sortedByDescending { it.sendingTimestamp }
                .mapNotNull { wrapResponse(it) }

        return ResponseEntity.ok(ConversationsResponse(sortedConversations))
    }

}