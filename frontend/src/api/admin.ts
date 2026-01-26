import apiClient from './client'
import type {
  AdminApplicationDto,
  ApproveApplicationRequest,
  ApproveApplicationResponse,
  RejectApplicationRequest,
  RejectApplicationResponse,
  ApplicationStatus,
} from '@/types/application'
import type {
  AdminAdoptionDto,
  ConfirmAdoptionRequest,
  ConfirmAdoptionResponse,
  CancelAdoptionRequest,
  CancelAdoptionResponse,
  ReturnPetRequest,
  ReturnPetResponse,
  AdoptionStatus,
} from '@/types/adoption'
import type {
  Pet,
  CreatePetRequest,
  UpdatePetRequest,
} from '@/types/pet'
import type { ImageUploadResponse } from '@/types/api'

// ==================== Applications ====================

export const fetchApplications = async (params?: {
  status?: ApplicationStatus
  petId?: number
}): Promise<AdminApplicationDto[]> => {
  const response = await apiClient.get<AdminApplicationDto[]>(
    '/admin/applications',
    { params }
  )
  return response.data
}

export const approveApplication = async (
  applicationId: number,
  data: ApproveApplicationRequest
): Promise<ApproveApplicationResponse> => {
  const response = await apiClient.post<ApproveApplicationResponse>(
    `/admin/applications/${applicationId}/approve`,
    data
  )
  return response.data
}

export const rejectApplication = async (
  applicationId: number,
  data: RejectApplicationRequest
): Promise<RejectApplicationResponse> => {
  const response = await apiClient.post<RejectApplicationResponse>(
    `/admin/applications/${applicationId}/reject`,
    data
  )
  return response.data
}

// ==================== Adoptions ====================

export const fetchAdoptions = async (params?: {
  status?: AdoptionStatus
  petId?: number
}): Promise<AdminAdoptionDto[]> => {
  const response = await apiClient.get<AdminAdoptionDto[]>(
    '/admin/adoptions',
    { params }
  )
  return response.data
}

export const confirmAdoption = async (
  adoptionId: number,
  data: ConfirmAdoptionRequest
): Promise<ConfirmAdoptionResponse> => {
  const response = await apiClient.post<ConfirmAdoptionResponse>(
    `/admin/adoptions/${adoptionId}/confirm`,
    data
  )
  return response.data
}

export const cancelAdoption = async (
  adoptionId: number,
  data: CancelAdoptionRequest
): Promise<CancelAdoptionResponse> => {
  const response = await apiClient.post<CancelAdoptionResponse>(
    `/admin/adoptions/${adoptionId}/cancel`,
    data
  )
  return response.data
}

export const returnPet = async (
  adoptionId: number,
  data: ReturnPetRequest
): Promise<ReturnPetResponse> => {
  const response = await apiClient.post<ReturnPetResponse>(
    `/admin/adoptions/${adoptionId}/return`,
    data
  )
  return response.data
}

// ==================== Pets ====================

export const fetchAdminPets = async (params?: {
  includeDeleted?: boolean
}): Promise<Pet[]> => {
  const response = await apiClient.get<Pet[]>('/admin/pets', { params })
  return response.data
}

export const createPet = async (data: CreatePetRequest): Promise<Pet> => {
  const response = await apiClient.post<Pet>('/admin/pets', data)
  return response.data
}

export const updatePet = async (
  petId: number,
  data: UpdatePetRequest
): Promise<Pet> => {
  const response = await apiClient.put<Pet>(`/admin/pets/${petId}`, data)
  return response.data
}

export const deletePet = async (petId: number): Promise<void> => {
  await apiClient.delete(`/admin/pets/${petId}`)
}

// ==================== Images ====================

export const uploadImage = async (file: File): Promise<ImageUploadResponse> => {
  const formData = new FormData()
  formData.append('image', file)

  const response = await apiClient.post<ImageUploadResponse>(
    '/admin/images',
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }
  )
  return response.data
}
