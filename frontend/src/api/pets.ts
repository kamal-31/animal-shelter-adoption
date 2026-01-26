import apiClient from './client'
import type { Pet, Species, PetStatus } from '@/types/pet'

/**
 * Fetch pets with optional filters
 */
export const fetchPets = async (params?: {
  status?: PetStatus
  speciesId?: number
}): Promise<Pet[]> => {
  const response = await apiClient.get<Pet[]>('/pets', { params })
  return response.data
}

/**
 * Fetch single pet by ID
 */
export const fetchPetById = async (id: number): Promise<Pet> => {
  const response = await apiClient.get<Pet>(`/pets/${id}`)
  return response.data
}

/**
 * Fetch all species
 */
export const fetchSpecies = async (): Promise<Species[]> => {
  const response = await apiClient.get<Species[]>('/species')
  return response.data
}