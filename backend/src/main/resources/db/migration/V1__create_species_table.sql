-- ========================================
-- Table: species
-- Purpose: Reference data for pet species
-- ========================================

CREATE TABLE species (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Comments
COMMENT ON TABLE species IS 'Reference table for pet species (Dog, Cat, etc.)';
COMMENT ON COLUMN species.name IS 'Unique species name';

-- No created_at/updated_at needed (reference data rarely changes)