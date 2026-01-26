-- ========================================
-- Triggers for Auto-Update and Validation
-- ========================================

-- Function: Auto-update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply to pets table
CREATE TRIGGER update_pets_updated_at 
    BEFORE UPDATE ON pets
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Apply to applicants table
CREATE TRIGGER update_applicants_updated_at 
    BEFORE UPDATE ON applicants
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON FUNCTION update_updated_at_column() IS 
    'Automatically updates updated_at column to current timestamp on row modification';


-- Function: Validate adoption_history applicant_id consistency
CREATE OR REPLACE FUNCTION validate_adoption_history_applicant()
RETURNS TRIGGER AS $$
DECLARE
    v_application_applicant_id BIGINT;
BEGIN
    -- Get the applicant_id from the referenced application
    SELECT applicant_id INTO v_application_applicant_id
    FROM applications
    WHERE id = NEW.application_id;
    
    -- Check if application exists
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Application with id % does not exist', NEW.application_id;
    END IF;
    
    -- Check if applicant_id matches
    IF v_application_applicant_id != NEW.applicant_id THEN
        RAISE EXCEPTION 
            'Data integrity violation: adoption_history.applicant_id (%) does not match applications.applicant_id (%) for application_id %',
            NEW.applicant_id,
            v_application_applicant_id,
            NEW.application_id;
    END IF;
    
    -- All checks passed
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply to adoption_history table
CREATE TRIGGER verify_adoption_history_applicant
    BEFORE INSERT OR UPDATE ON adoption_history
    FOR EACH ROW
    EXECUTE FUNCTION validate_adoption_history_applicant();

-- Comments
COMMENT ON FUNCTION validate_adoption_history_applicant() IS 
    'Validates that adoption_history.applicant_id matches applications.applicant_id for data consistency';