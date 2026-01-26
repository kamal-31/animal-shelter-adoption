import React, { useState } from 'react'
import { Container } from '@/components/layout/Container'
import { PetGrid } from '@/components/pets/PetGrid'
import { Spinner } from '@/components/common/Spinner'
import { Alert } from '@/components/common/Alert'
import { usePets, useSpecies } from '@/hooks/usePets'
import { PetStatus } from '@/types/pet'

export const PetListPage: React.FC = () => {
  const [statusFilter, setStatusFilter] = useState<PetStatus | undefined>(undefined)
  const [speciesFilter, setSpeciesFilter] = useState<number | undefined>(undefined)

  const { data: pets, isLoading, error } = usePets(statusFilter, speciesFilter)
  const { data: species } = useSpecies()

  return (
    <div className="min-h-screen py-8">
      <Container>
        <h1 className="text-3xl font-bold mb-6">Available Pets</h1>

        {/* Filters */}
        <div className="flex gap-4 mb-8">
          <select
            value={speciesFilter ?? ''}
            onChange={(e) =>
              setSpeciesFilter(e.target.value ? Number(e.target.value) : undefined)
            }
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Species</option>
            {species?.map((s) => (
              <option key={s.id} value={s.id}>
                {s.name}
              </option>
            ))}
          </select>

          <select
            value={statusFilter ?? ''}
            onChange={(e) =>
              setStatusFilter(e.target.value ? (e.target.value as PetStatus) : undefined)
            }
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Status</option>
            <option value="AVAILABLE">Available</option>
            <option value="PENDING">Pending</option>
            <option value="ADOPTED">Adopted</option>
          </select>
        </div>

        {/* Loading */}
        {isLoading && (
          <div className="flex justify-center py-12">
            <Spinner size="lg" />
          </div>
        )}

        {/* Error */}
        {error && (
          <Alert type="error" message="Failed to load pets. Please try again." />
        )}

        {/* Results */}
        {pets && <PetGrid pets={pets} />}
      </Container>
    </div>
  )
}