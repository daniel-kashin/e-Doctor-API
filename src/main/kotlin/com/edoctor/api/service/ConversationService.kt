package com.edoctor.api.service

import com.edoctor.api.configuration.socket.WebSocketPrincipal
import com.edoctor.api.entities.network.TextMessage
import com.edoctor.api.entities.storage.Conversation
import com.edoctor.api.entities.storage.Message
import com.edoctor.api.repositories.ConversationRepository
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
import com.edoctor.api.utils.currentUnixTime
import mu.KotlinLogging
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.UUID.randomUUID

@Repository
@Transactional
class ConversationService {

    private val log = logger {}

    @Autowired
    private lateinit var conversationRepository: ConversationRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Transactional
    fun addMessage(currentUserEmail: String, currentUserIsPatient: Boolean, message: TextMessage): Boolean {
        val patientEmail = if (currentUserIsPatient) currentUserEmail else message.recipientEmail
        val doctorEmail = if (currentUserIsPatient) message.recipientEmail else currentUserEmail

        val conversation = conversationRepository.findByPatientEmailAndDoctorEmail(patientEmail, doctorEmail)
                ?: run {
                    val patient = patientRepository.findByEmail(patientEmail) ?: return false
                    val doctor = doctorRepository.findByEmail(doctorEmail) ?: return false
                    Conversation(
                            givenUuid = randomUUID(),
                            patient = patient,
                            doctor = doctor,
                            messages = mutableListOf()
                    )
                }

        conversation.run {
            messages.add(
                    Message(
                            givenUuid = UUID.fromString(message.uuid),
                            timestamp = currentUnixTime(),
                            text = message.text,
                            isFromPatient = message.senderEmail == patientEmail,
                            conversation = this
                    ).also {
                        log.info {
                            "saveMessage(givenUuid=${message.uuid}, text=${message.text}, timestamp=${message.sendingTimestamp}"
                        }
                    }
            )
            conversationRepository.save(this)
        }

        return true
    }
}