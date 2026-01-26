-- ========================================
-- Table: adoption_history
-- Purpose: Track adoption lifecycle
-- ========================================

CREATE TABLE adoption_history (
    id             BIGSERIAL PRIMARY KEY,
    pet_id         BIGINT NOT NULL,
    application_id BIGINT NOT NULL,
    applicant_id   BIGINT NOT NULL,
    adopted_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    returned_at    TIMESTAMP,
    return_reason  TEXT,
    status         VARCHAR(20) NOT NULL DEFAULT 'PENDING_PICKUP',
    notes          TEXT,
    
    CONSTRAINT fk_pet FOREIGN KEY (pet_id) REFERENCES pets(id),
    CONSTRAINT fk_application FOREIGN KEY (application_id) REFERENCES applications(id),
    CONSTRAINT fk_applicant FOREIGN KEY (applicant_id) REFERENCES applicants(id),
    CONSTRAINT check_adoption_status CHECK (
        status IN ('PENDING_PICKUP', 'ACTIVE', 'RETURNED', 'CANCELLED')
    )
);

-- Indexes
CREATE INDEX idx_adoption_history_pet_id ON adoption_history(pet_id);
CREATE INDEX idx_adoption_history_application_id ON adoption_history(application_id);
CREATE INDEX idx_adoption_history_applicant_id ON adoption_history(applicant_id);
CREATE INDEX idx_adoption_history_status ON adoption_history(status);

-- Comments
COMMENT ON TABLE adoption_history IS 'Complete adoption lifecycle tracking';
COMMENT ON COLUMN adoption_history.applicant_id IS 'Denormalized from applications for query performance - validated by trigger';
COMMENT ON COLUMN adoption_history.status IS 'PENDING_PICKUP, ACTIVE, RETURNED, CANCELLED';
COMMENT ON COLUMN adoption_history.adopted_at IS 'When admin approved (not when family picked up)';
COMMENT ON COLUMN adoption_history.returned_at IS 'When pet was returned to shelter (if applicable)';