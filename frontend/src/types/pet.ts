export enum PetStatus {
  AVAILABLE = 'AVAILABLE',
  PENDING = 'PENDING',
  ADOPTED = 'ADOPTED',
}

export interface Pet {
  id: number
  name: string
  species: string
  age: number
  imageUrl: string | null
  description: string | null
  status: PetStatus
  pendingApplicationCount: number
  createdAt?: string
  deletedAt?: string | null
}

export interface Species {
  id: number
  name: string
}

export interface CreatePetRequest {
  name: string
  speciesId: number
  age: number
  imageUrl?: string | null
  description?: string | null
}

export interface UpdatePetRequest {
  name?: string
  age?: number
  imageUrl?: string | null
  description?: string | null
}