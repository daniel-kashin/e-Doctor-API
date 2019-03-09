package com.edoctor.api.repositories

import com.edoctor.api.entities.storage.ConversationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConversationRepository : JpaRepository<ConversationEntity, String> {

    fun findByPatientUuidAndDoctorUuid(patientUuid: String, doctorUuid: String) : ConversationEntity?

    fun findByPatientEmailAndDoctorEmail(patientEmail: String, doctorEmail: String) : ConversationEntity?

    fun findByPatientUuid(patientUuid: String) : List<ConversationEntity>

    fun findByDoctorUuid(doctorUuid: String) : List<ConversationEntity>

}