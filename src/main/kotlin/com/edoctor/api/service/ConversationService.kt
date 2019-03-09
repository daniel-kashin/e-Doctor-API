package com.edoctor.api.service

import com.edoctor.api.entities.storage.ConversationEntity
import com.edoctor.api.entities.storage.MessageEntity
import com.edoctor.api.repositories.ConversationRepository
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
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
    fun getConversation(
            currentUserEmail: String,
            recipientEmail: String,
            currentUserIsPatient: Boolean
    ): ConversationEntity? {
        val patientEmail = if (currentUserIsPatient) currentUserEmail else recipientEmail
        val doctorEmail = if (currentUserIsPatient) recipientEmail else currentUserEmail

        return conversationRepository.findByPatientEmailAndDoctorEmail(patientEmail, doctorEmail)
                ?: run {
                    val patient = patientRepository.findByEmail(patientEmail) ?: return null
                    val doctor = doctorRepository.findByEmail(doctorEmail) ?: return null
                    ConversationEntity(
                            givenUuid = randomUUID(),
                            patient = patient,
                            doctor = doctor,
                            messages = mutableListOf()
                    )
                }
    }

    @Transactional
    fun addMessage(
            messageEntity: MessageEntity,
            conversationEntity: ConversationEntity
    ) {
        conversationEntity.messages.add(messageEntity)
        conversationRepository.save(conversationEntity)
    }

}