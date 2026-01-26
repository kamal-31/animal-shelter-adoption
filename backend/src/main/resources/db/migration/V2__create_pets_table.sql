-- ========================================
-- Table: pets
-- Purpose: Animals available for adoption
-- ========================================

CREATE TABLE pets (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    species_id  INT NOT NULL,
    age         INT NOT NULL,
    image_url   VARCHAR(500),
    description TEXT,
    status      VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP,
    
    CONSTRAINT fk_species FOREIGN KEY (species_id) REFERENCES species(id),
    CONSTRAINT check_status CHECK (status IN ('AVAILABLE', 'PENDING', 'ADOPTED')),
    CONSTRAINT check_age CHECK (age >= 0)
);

-- Indexes
CREATE INDEX idx_pets_species_id ON pets(species_id);
CREATE INDEX idx_pets_status ON pets(status);
CREATE INDEX idx_pets_deleted_at ON pets(deleted_at);

-- Comments
COMMENT ON TABLE pets IS 'Animals available for adoption';
COMMENT ON COLUMN pets.status IS 'AVAILABLE, PENDING (has applications), ADOPTED';
COMMENT ON COLUMN pets.deleted_at IS 'Soft delete timestamp - NULL means active';
COMMENT ON COLUMN pets.age IS 'Age in years (0 for < 1 year old)';