export enum AdoptionStatus {
  PENDING_PICKUP = 'PENDING_PICKUP',
  ACTIVE = 'ACTIVE',
  RETURNED = 'RETURNED',
  CANCELLED = 'CANCELLED',
}

export interface AdminAdoptionDto {
  id: number
  petId: number
  petName: string
  applicationId: number
  applicantId: number
  applicantName: string
  applicantEmail: string
  status: AdoptionStatus
  adoptedAt: string
  returnedAt: string | null
  returnReason: string | null
  notes: string | null
}

export interface ConfirmAdoptionRequest {
  notes?: string | null
}

export interface ConfirmAdoptionResponse {
  adoptionHistoryId: number
  adoptionStatus: AdoptionStatus
  petId: number
  petStatus: string
  rejectedApplicationCount: number
  message: string
}

export interface CancelAdoptionRequest {
  reason: string
}

export interface CancelAdoptionResponse {
  adoptionHistoryId: number
  adoptionStatus: AdoptionStatus
  applicationId: number
  applicationStatus: string
  petId: number
  petStatus: string
  message: string
}

export interface ReturnPetRequest {
  returnReason: string
  notes?: string | null
}

export interface ReturnPetResponse {
  adoptionHistoryId: number
  adoptionStatus: AdoptionStatus
  petId: number
  petStatus: string
  returnedAt: string
  message: string
}