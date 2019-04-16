package com.edoctor.api.service

import com.edoctor.api.entities.storage.ConversationEntity
import com.edoctor.api.entities.storage.MessageEntity
import com.edoctor.api.repositories.ConversationRepository
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.MessageRepository
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
    private lateinit var messageRepository: MessageRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Transactional
    fun getConversation(
            currentUserUuid: String,
            recipientUuid: String,
            currentUserIsPatient: Boolean
    ): ConversationEntity? {
        val patientUuid = if (currentUserIsPatient) currentUserUuid else recipientUuid
        val doctorUuid = if (currentUserIsPatient) recipientUuid else currentUserUuid

        return conversationRepository.findByPatientUuidAndDoctorUuid(patientUuid, doctorUuid)
                ?: run {
                    val patient = patientRepository.findById(patientUuid).orElse(null) ?: return null
                    val doctor = doctorRepository.findById(doctorUuid).orElse(null) ?: return null
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
        if (!conversationRepository.existsById(conversationEntity.id)) {
            conversationRepository.save(conversationEntity)
        }
        messageRepository.save(messageEntity)
    }

}