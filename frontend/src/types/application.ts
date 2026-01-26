import { PetStatus } from './pet'

export enum ApplicationStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  ADOPTION_CANCELLED = 'ADOPTION_CANCELLED',
}

export interface SubmitApplicationRequest {
  petId: number
  applicantName: string
  email: string
  phone?: string | null
  reason: string
}

export interface SubmitApplicationResponse {
  id: number
  petId: number
  petName: string
  applicantName: string
  status: ApplicationStatus
  submittedAt: string
  message: string
}

export interface AdminApplicationDto {
  id: number
  petId: number
  petName: string
  petSpecies: string
  petImageUrl: string | null
  petStatus: PetStatus
  applicantId: number
  applicantName: string
  applicantEmail: string
  applicantPhone: string | null
  reason: string
  status: ApplicationStatus
  submittedAt: string
  reviewedAt: string | null
  reviewedBy: string | null
}

export interface ApproveApplicationRequest {
  reviewedBy?: string | null
}

export interface ApproveApplicationResponse {
  applicationId: number
  status: ApplicationStatus
  adoptionHistoryId: number
  adoptionStatus: string
  message: string
}

export interface RejectApplicationRequest {
  reviewedBy?: string | null
}

export interface RejectApplicationResponse {
  applicationId: number
  status: ApplicationStatus
  petStatus: PetStatus
  message: string
}