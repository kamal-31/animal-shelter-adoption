package com.animalshelter.exception

// Resource not found exceptions
class PetNotFoundException(petId: Long) :
    RuntimeException("Pet with id $petId not found")

class ApplicationNotFoundException(applicationId: Long) :
    RuntimeException("Application with id $applicationId not found")

class AdoptionNotFoundException(adoptionId: Long) :
    RuntimeException("Adoption with id $adoptionId not found")

class SpeciesNotFoundException(speciesId: Int) :
    RuntimeException("Species with id $speciesId not found")

// Business rule violations
class BusinessRuleViolationException(message: String) :
    RuntimeException(message)

class DuplicateApplicationException(message: String) :
    RuntimeException(message)

class ImageUploadException(message: String) :
    RuntimeException(message)