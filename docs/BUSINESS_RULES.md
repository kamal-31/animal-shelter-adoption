# Business Rules & Constraints

## Pet Status Lifecycle

### States

```
AVAILABLE → PENDING → ADOPTED
     ↑         ↓
     └─────────┘ (if all applications rejected)
```

**AVAILABLE:**

- Pet has no pending or approved applications
- Users can submit applications

**PENDING:**

- Pet has at least one pending or approved application
- Users can still submit applications (waitlist)

**ADOPTED:**

- Pet has been picked up by adopter
- Application form disabled
- Can be returned → transitions back to AVAILABLE

### Transitions

| From      | To        | Trigger                     |
|-----------|-----------|-----------------------------|
| AVAILABLE | PENDING   | First application submitted |
| PENDING   | AVAILABLE | All applications rejected   |
| PENDING   | ADOPTED   | Adoption confirmed (pickup) |
| ADOPTED   | AVAILABLE | Pet returned to shelter     |

## Application Status Lifecycle

### States

```
PENDING → APPROVED → (Adoption ACTIVE)
   ↓
REJECTED

APPROVED → ADOPTION_CANCELLED (if family doesn't show up)
```

**PENDING:**

- Initial state when submitted
- Awaiting admin review

**APPROVED:**

- Admin has approved the application
- Creates adoption history with PENDING_PICKUP
- **Only ONE application can be approved per pet**

**REJECTED:**

- Admin has rejected the application
- If no other active applications, pet → AVAILABLE

**ADOPTION_CANCELLED:**

- Family didn't show up for pickup
- Adoption history status → CANCELLED
- Pet reverts to appropriate status

## Adoption Workflow

### States

```
PENDING_PICKUP → ACTIVE → RETURNED
       ↓
   CANCELLED
```

**PENDING_PICKUP:**

- Created when application is approved
- Waiting for family to pick up pet
- Admin actions: Confirm or Cancel

**ACTIVE:**

- Family has picked up the pet
- Pet status → ADOPTED
- All other pending applications → REJECTED
- Admin action: Mark as returned

**RETURNED:**

- Pet returned to shelter
- Pet status → AVAILABLE
- Available for re-adoption

**CANCELLED:**

- Family didn't complete adoption
- Application status → ADOPTION_CANCELLED
- Pet returns to PENDING or AVAILABLE

## Validation Rules

### Application Submission

**Constraints:**

- ✅ Name: Required, 1-200 characters
- ✅ Email: Required, valid format, unique per pet
- ✅ Phone: Optional, 0-20 characters
- ✅ Reason: Required, 50-5000 characters

**Business Rules:**

- ❌ Cannot apply for ADOPTED pets
- ❌ Cannot submit duplicate application (same email + pet)
- ✅ Can apply for PENDING pets (join waitlist)

### Application Approval

**Pre-conditions:**

- ✅ Application must be in PENDING status
- ✅ Pet must not be ADOPTED
- ❌ **No other APPROVED applications exist for this pet**
- ❌ **No PENDING_PICKUP or ACTIVE adoptions exist for this pet**

**Post-conditions:**

- Application status → APPROVED
- Adoption history created with PENDING_PICKUP
- reviewedAt and reviewedBy recorded

### Adoption Confirmation

**Pre-conditions:**

- Adoption must be in PENDING_PICKUP status

**Post-conditions:**

- Adoption status → ACTIVE
- Pet status → ADOPTED
- All other pending/approved applications → REJECTED

### Pet Return

**Pre-conditions:**

- Adoption must be in ACTIVE status
- Return reason required (10-5000 characters)

**Post-conditions:**

- Adoption status → RETURNED
- returnedAt timestamp recorded
- Pet status → AVAILABLE (ready for re-adoption)

## Data Integrity Rules

### Foreign Key Constraints

```
applications.pet_id → pets.id
applications.applicant_id → applicants.id
adoption_history.pet_id → pets.id
adoption_history.application_id → applications.id
adoption_history.applicant_id → applicants.id
```

### Unique Constraints

- `applicants.email` (unique)
- `applications (pet_id, applicant_id)` (unique together)
- `species.name` (unique)

### Check Constraints

- Pet age: 0-50 years
- Pet status: AVAILABLE | PENDING | ADOPTED
- Application status: PENDING | APPROVED | REJECTED | ADOPTION_CANCELLED
- Adoption status: PENDING_PICKUP | ACTIVE | RETURNED | CANCELLED

## Soft Delete Behavior

**Pets:**

- Soft deleted via `deleted_at` timestamp
- Not visible in public listings
- Preserves historical data
- Applications and adoptions remain intact

**Constraints:**

- Cannot delete pet with PENDING or APPROVED applications
- Cannot delete pet with ACTIVE adoption

## Concurrent Access Handling

**Scenario:** Two admins approve different applications for same pet simultaneously

**Solution:**

- Database-level unique constraint prevents double approval
- Application service validates before approval
- First approval succeeds, second gets 422 error
- Error message directs admin to reject existing approval first

## Edge Cases

### Family applies multiple times

- ❌ Blocked by unique constraint (pet_id, applicant_id)
- Error: "You have already applied for this pet"

### Pet returned after adoption

- ✅ Adoption status → RETURNED
- ✅ Pet status → AVAILABLE
- ✅ Pet can be re-adopted by different family

### Application approved but family never picks up

- ✅ Admin cancels adoption
- ✅ Application status → ADOPTION_CANCELLED
- ✅ Pet returns to PENDING (if other apps) or AVAILABLE

### All applications rejected

- ✅ Pet status automatically reverts to AVAILABLE
- ✅ Trigger on last rejection checks for active applications