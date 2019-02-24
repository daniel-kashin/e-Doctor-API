package com.edoctor.api.repositories

import com.edoctor.api.entities.storage.Conversation
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConversationRepository : JpaRepository<Conversation, String> {

    fun findByPatientUuidAndDoctorUuid(patientUuid: String, doctorUuid: String) : Conversation?

    fun findByPatientEmailAndDoctorEmail(patientEmail: String, doctorEmail: String) : Conversation?

    fun findByPatientUuid(patientUuid: String) : List<Conversation>

    fun findByDoctorUuid(doctorUuid: String) : List<Conversation>

}