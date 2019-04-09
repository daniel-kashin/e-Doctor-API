package com.edoctor.api.repositories

import com.edoctor.api.entities.storage.DoctorEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DoctorRepository : JpaRepository<DoctorEntity, String> {

    fun findByEmail(email: String) : DoctorEntity?

    fun findByFullNameIgnoreCaseContainingOrSpecializationIgnoreCaseContaining(
            fullName: String,
            specialization: String
    ): List<DoctorEntity>

    fun existsByEmail(email: String) : Boolean

}