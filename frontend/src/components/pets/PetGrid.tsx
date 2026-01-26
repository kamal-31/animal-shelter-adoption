import React from 'react'
import { PetCard } from './PetCard'
import type { Pet } from '@/types/pet'

interface PetGridProps {
  pets: Pet[]
}

export const PetGrid: React.FC<PetGridProps> = ({ pets }) => {
  if (pets.length === 0) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500 text-lg">No pets found</p>
      </div>
    )
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {pets.map((pet) => (
        <PetCard key={pet.id} pet={pet} />
      ))}
    </div>
  )
}