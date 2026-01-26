import { useQuery } from '@tanstack/react-query'
import { fetchPets, fetchPetById, fetchSpecies } from '@/api/pets'
import type { PetStatus } from '@/types/pet'

/**
 * Fetch pets with optional filters
 */
export const usePets = (status?: PetStatus, speciesId?: number) => {
  return useQuery({
    queryKey: ['pets', status, speciesId],
    queryFn: () => fetchPets({ status, speciesId }),
  })
}

/**
 * Fetch single pet by ID
 */
export const usePet = (id: number) => {
  return useQuery({
    queryKey: ['pets', id],
    queryFn: () => fetchPetById(id),
    enabled: !!id,
  })
}

/**
 * Fetch all species
 */
export const useSpecies = () => {
  return useQuery({
    queryKey: ['species'],
    queryFn: fetchSpecies,
    staleTime: 30 * 60 * 1000, // 30 minutes (rarely changes)
  })
}