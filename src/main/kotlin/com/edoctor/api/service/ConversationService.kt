package com.edoctor.api.service

import com.edoctor.api.configuration.socket.WebSocketPrincipal
import com.edoctor.api.entities.network.TextMessage
import com.edoctor.api.entities.storage.Conversation
import com.edoctor.api.entities.storage.Message
import com.edoctor.api.repositories.ConversationRepository
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class ConversationService {

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
                    Conversation(patient, doctor, mutableListOf())
                }

        conversation.run {
            messages.add(
                    Message(
                            timestamp = System.currentTimeMillis() / 1000,
                            text = message.text,
                            isFromPatient = message.senderEmail == patientEmail,
                            conversation = this
                    )
            )
            conversationRepository.save(this)
        }

        return true
    }
}