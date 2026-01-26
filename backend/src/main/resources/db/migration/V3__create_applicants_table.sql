-- ========================================
-- Table: applicants
-- Purpose: People who apply for adoption
-- ========================================

CREATE TABLE applicants (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(200) NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    phone      VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_applicants_email ON applicants(email);

-- Comments
COMMENT ON TABLE applicants IS 'People who submit adoption applications';
COMMENT ON COLUMN applicants.email IS 'Unique identifier - one email per applicant';
COMMENT ON COLUMN applicants.phone IS 'Optional phone number';