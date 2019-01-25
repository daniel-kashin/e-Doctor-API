package com.edoctor.api.repositories

import com.edoctor.api.entities.storage.Doctor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DoctorRepository : JpaRepository<Doctor, String> {

    fun findByEmail(email: String) : Doctor?

    fun existsByEmail(email: String) : Boolean

}