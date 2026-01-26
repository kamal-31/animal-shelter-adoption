-- ========================================
-- Table: applications
-- Purpose: Adoption requests from users
-- ========================================

CREATE TABLE applications (
    id           BIGSERIAL PRIMARY KEY,
    pet_id       BIGINT NOT NULL,
    applicant_id BIGINT NOT NULL,
    reason       TEXT NOT NULL,
    status       VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at  TIMESTAMP,
    reviewed_by  VARCHAR(100),
    
    CONSTRAINT fk_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    CONSTRAINT fk_applicant FOREIGN KEY (applicant_id) REFERENCES applicants(id),
    CONSTRAINT check_application_status CHECK (
        status IN ('PENDING', 'APPROVED', 'REJECTED', 'ADOPTION_CANCELLED')
    ),
    CONSTRAINT unique_pet_applicant UNIQUE(pet_id, applicant_id)
);

-- Indexes
CREATE INDEX idx_applications_pet_id ON applications(pet_id);
CREATE INDEX idx_applications_applicant_id ON applications(applicant_id);
CREATE INDEX idx_applications_status ON applications(status);
CREATE INDEX idx_applications_submitted_at ON applications(submitted_at DESC);

-- Comments
COMMENT ON TABLE applications IS 'Adoption applications submitted by users';
COMMENT ON COLUMN applications.status IS 'PENDING, APPROVED, REJECTED, ADOPTION_CANCELLED';
COMMENT ON COLUMN applications.reason IS 'Why applicant wants to adopt this pet';
COMMENT ON CONSTRAINT unique_pet_applicant ON applications IS 'Prevent same person applying twice to same pet';