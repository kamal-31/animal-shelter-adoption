import React from 'react'
import { useNavigate } from 'react-router-dom'
import { Card } from '@/components/common/Card'
import { Badge } from '@/components/common/Badge'
import { Button } from '@/components/common/Button'
import type { Pet, PetStatus } from '@/types/pet'

interface PetCardProps {
  pet: Pet
}

const getStatusVariant = (status: PetStatus) => {
  switch (status) {
    case 'AVAILABLE':
      return 'success'
    case 'PENDING':
      return 'warning'
    case 'ADOPTED':
      return 'info'
    default:
      return 'default'
  }
}

export const PetCard: React.FC<PetCardProps> = ({ pet }) => {
  const navigate = useNavigate()

  return (
    <Card onClick={() => navigate(`/pets/${pet.id}`)} className="overflow-hidden">
      {/* Image */}
      <div className="relative h-48">
        <img
          src={pet.imageUrl || '/placeholder-pet.jpg'}
          alt={pet.name}
          className="w-full h-full object-cover"
        />
        <Badge
          variant={getStatusVariant(pet.status)}
          className="absolute top-2 right-2"
        >
          {pet.status}
        </Badge>
      </div>

      {/* Content */}
      <div className="p-4">
        <h3 className="text-lg font-semibold mb-1 text-gray-900">{pet.name}</h3>
        <p className="text-sm text-gray-600 mb-2">
          {pet.species} • {pet.age} {pet.age === 1 ? 'year' : 'years'} old
        </p>

        {pet.description && (
          <p className="text-sm text-gray-700 line-clamp-2 mb-3">
            {pet.description}
          </p>
        )}

        {pet.status === 'AVAILABLE' && (
          <Button
            variant="primary"
            size="sm"
            onClick={(e) => {
              e.stopPropagation()
              navigate(`/pets/${pet.id}`)
            }}
            className="w-full"
          >
            Apply to Adopt
          </Button>
        )}

        {pet.pendingApplicationCount > 0 && (
          <p className="text-xs text-gray-500 mt-2">
            {pet.pendingApplicationCount} pending{' '}
            {pet.pendingApplicationCount === 1 ? 'application' : 'applications'}
          </p>
        )}
      </div>
    </Card>
  )
}