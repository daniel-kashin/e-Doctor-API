package com.edoctor.api.repositories

import com.edoctor.api.entities.storage.Patient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PatientRepository : JpaRepository<Patient, String> {

    fun findByEmail(email: String) : Patient?

    fun existsByEmail(email: String) : Boolean

}
