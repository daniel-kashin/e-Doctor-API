package com.edoctor.api.repositories

import com.edoctor.api.entities.storage.PatientEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PatientRepository : JpaRepository<PatientEntity, String> {

    fun findByEmail(email: String) : PatientEntity?

    fun existsByEmail(email: String) : Boolean

}
