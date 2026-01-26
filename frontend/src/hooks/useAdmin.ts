import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  fetchApplications,
  approveApplication,
  rejectApplication,
  fetchAdoptions,
  confirmAdoption,
  cancelAdoption,
  returnPet,
  fetchAdminPets,
  createPet,
  updatePet,
  deletePet,
  uploadImage,
} from '@/api/admin'
import type {
  ApplicationStatus,
  ApproveApplicationRequest,
  RejectApplicationRequest,
} from '@/types/application'
import type {
  AdoptionStatus,
  ConfirmAdoptionRequest,
  CancelAdoptionRequest,
  ReturnPetRequest,
} from '@/types/adoption'
import type { CreatePetRequest, UpdatePetRequest } from '@/types/pet'

// ==================== Applications ====================

export const useApplications = (params?: {
  status?: ApplicationStatus
  petId?: number
}) => {
  return useQuery({
    queryKey: ['admin', 'applications', params],
    queryFn: () => fetchApplications(params),
  })
}

export const useApproveApplication = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({
      applicationId,
      data,
    }: {
      applicationId: number
      data: ApproveApplicationRequest
    }) => approveApplication(applicationId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'applications'] })
      queryClient.invalidateQueries({ queryKey: ['admin', 'adoptions'] })
      queryClient.invalidateQueries({ queryKey: ['pets'] })
    },
  })
}

export const useRejectApplication = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({
      applicationId,
      data,
    }: {
      applicationId: number
      data: RejectApplicationRequest
    }) => rejectApplication(applicationId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'applications'] })
      queryClient.invalidateQueries({ queryKey: ['pets'] })
    },
  })
}

// ==================== Adoptions ====================

export const useAdoptions = (params?: {
  status?: AdoptionStatus
  petId?: number
}) => {
  return useQuery({
    queryKey: ['admin', 'adoptions', params],
    queryFn: () => fetchAdoptions(params),
  })
}

export const useConfirmAdoption = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({
      adoptionId,
      data,
    }: {
      adoptionId: number
      data: ConfirmAdoptionRequest
    }) => confirmAdoption(adoptionId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'adoptions'] })
      queryClient.invalidateQueries({ queryKey: ['admin', 'applications'] })
      queryClient.invalidateQueries({ queryKey: ['pets'] })
    },
  })
}

export const useCancelAdoption = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({
      adoptionId,
      data,
    }: {
      adoptionId: number
      data: CancelAdoptionRequest
    }) => cancelAdoption(adoptionId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'adoptions'] })
      queryClient.invalidateQueries({ queryKey: ['admin', 'applications'] })
      queryClient.invalidateQueries({ queryKey: ['pets'] })
    },
  })
}

export const useReturnPet = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({
      adoptionId,
      data,
    }: {
      adoptionId: number
      data: ReturnPetRequest
    }) => returnPet(adoptionId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'adoptions'] })
      queryClient.invalidateQueries({ queryKey: ['pets'] })
    },
  })
}

// ==================== Pets ====================

export const useAdminPets = (includeDeleted?: boolean) => {
  return useQuery({
    queryKey: ['admin', 'pets', includeDeleted],
    queryFn: () => fetchAdminPets({ includeDeleted }),
  })
}

export const useCreatePet = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreatePetRequest) => createPet(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pets'] })
      queryClient.invalidateQueries({ queryKey: ['admin', 'pets'] })
    },
  })
}

export const useUpdatePet = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ petId, data }: { petId: number; data: UpdatePetRequest }) =>
      updatePet(petId, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['pets', variables.petId] })
      queryClient.invalidateQueries({ queryKey: ['pets'] })
      queryClient.invalidateQueries({ queryKey: ['admin', 'pets'] })
    },
  })
}

export const useDeletePet = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (petId: number) => deletePet(petId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pets'] })
      queryClient.invalidateQueries({ queryKey: ['admin', 'pets'] })
    },
  })
}

// ==================== Images ====================

export const useUploadImage = () => {
  return useMutation({
    mutationFn: (file: File) => uploadImage(file),
  })
}