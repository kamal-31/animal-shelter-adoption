package com.animalshelter.repository

import com.animalshelter.model.Applicant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ApplicantRepository : JpaRepository<Applicant, Long> {

    fun findByEmail(email: String): Applicant?

    fun existsByEmail(email: String): Boolean
}