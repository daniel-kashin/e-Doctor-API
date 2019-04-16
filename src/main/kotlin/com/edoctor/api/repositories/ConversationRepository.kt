package com.edoctor.api.repositories

import com.edoctor.api.entities.storage.ConversationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConversationRepository : JpaRepository<ConversationEntity, String> {

    fun findByPatientUuidAndDoctorUuid(patientUuid: String, doctorUuid: String) : ConversationEntity?

}