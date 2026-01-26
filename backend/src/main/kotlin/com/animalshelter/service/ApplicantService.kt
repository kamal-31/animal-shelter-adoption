package com.animalshelter.service

import com.animalshelter.model.Applicant
import com.animalshelter.repository.ApplicantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicantService(
    private val applicantRepository: ApplicantRepository
) {

    fun findById(id: Long): Applicant? {
        return applicantRepository.findById(id).orElse(null)
    }

    /**
     * Find or create applicant by email
     * If email exists, update name and phone if changed
     */
    @Transactional
    fun findOrCreate(name: String, email: String, phone: String?): Applicant {
        val existing = applicantRepository.findByEmail(email)

        if (existing != null) {
            // Update contact info if changed
            var updated = false

            if (existing.name != name) {
                existing.name = name
                updated = true
            }

            if (existing.phone != phone) {
                existing.phone = phone
                updated = true
            }

            return if (updated) {
                applicantRepository.save(existing)
            } else {
                existing
            }
        }

        // Create new applicant
        return applicantRepository.save(
            Applicant(
                name = name,
                email = email,
                phone = phone
            )
        )
    }
}