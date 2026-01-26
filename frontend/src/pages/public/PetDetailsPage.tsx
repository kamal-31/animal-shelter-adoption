import React, { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Container } from '@/components/layout/Container'
import { Badge } from '@/components/common/Badge'
import { Spinner } from '@/components/common/Spinner'
import { Alert } from '@/components/common/Alert'
import { Modal } from '@/components/common/Modal'
import { Button } from '@/components/common/Button'
import { ApplicationForm } from '@/components/pets/ApplicationForm'
import { usePet } from '@/hooks/usePets'
import { PetStatus } from '@/types/pet'

const getStatusVariant = (status: PetStatus) => {
  switch (status) {
    case 'AVAILABLE':
      return 'success'
    case 'PENDING':
      return 'warning'
    case 'ADOPTED':
      return 'info'
  }
}

export const PetDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { data: pet, isLoading, error } = usePet(Number(id))
  const [showSuccessModal, setShowSuccessModal] = useState(false)

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <Spinner size="lg" />
      </div>
    )
  }

  if (error || !pet) {
    return (
      <Container className="py-12">
        <Alert type="error" message="Pet not found" />
      </Container>
    )
  }

  return (
    <div className="min-h-screen py-8">
      <Container>
        {/* Back button */}
        <button
          onClick={() => navigate('/pets')}
          className="flex items-center text-blue-600 mb-6 hover:underline"
        >
          ← Back to All Pets
        </button>

        {/* Pet Details */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-12">
          {/* Image */}
          <div>
            <img
              src={pet.imageUrl || '/placeholder-pet.jpg'}
              alt={pet.name}
              className="w-full h-96 object-cover rounded-lg"
            />
          </div>

          {/* Info */}
          <div>
            <div className="flex items-start justify-between mb-4">
              <h1 className="text-3xl font-bold">{pet.name}</h1>
              <Badge variant={getStatusVariant(pet.status)} size="md">
                {pet.status}
              </Badge>
            </div>

            <div className="space-y-3 mb-6">
              <div className="flex">
                <span className="font-medium text-gray-700 w-32">Species:</span>
                <span className="text-gray-900">{pet.species}</span>
              </div>
              <div className="flex">
                <span className="font-medium text-gray-700 w-32">Age:</span>
                <span className="text-gray-900">
                  {pet.age} {pet.age === 1 ? 'year' : 'years'} old
                </span>
              </div>
              {pet.pendingApplicationCount > 0 && (
                <div className="flex">
                  <span className="font-medium text-gray-700 w-32">
                    Applications:
                  </span>
                  <span className="text-gray-900">
                    {pet.pendingApplicationCount} pending
                  </span>
                </div>
              )}
            </div>

            <div>
              <h2 className="text-xl font-semibold mb-2">About {pet.name}</h2>
              <p className="text-gray-700 whitespace-pre-line">
                {pet.description || 'No description available'}
              </p>
            </div>
          </div>
        </div>

        {/* Application Form */}
        {(pet.status === 'AVAILABLE' || pet.status === 'PENDING') && (
          <>
            {pet.status === 'PENDING' && (
              <>
                <Alert
                  type="info"
                  message={`${pet.name} has ${pet.pendingApplicationCount} pending application(s). You can still apply!`}
                />
                <br/>
              </>
            )}
            <ApplicationForm
              petId={pet.id}
              petName={pet.name}
              onSuccess={() => setShowSuccessModal(true)}
            />
          </>
        )}

        {pet.status === 'ADOPTED' && (
          <Alert
            type="warning"
            message={`${pet.name} has been adopted. Check out our other available pets!`}
          />
        )}

        {/* Success Modal */}
        <Modal
          isOpen={showSuccessModal}
          onClose={() => setShowSuccessModal(false)}
          title="Application Submitted!"
        >
          <div className="text-center py-4">
            <div className="text-6xl mb-4">✅</div>
            <p className="text-lg mb-4">
              Thank you for your application! We will review it and contact you
              soon.
            </p>
            <Button variant="primary" onClick={() => navigate('/pets')}>
              Browse More Pets
            </Button>
          </div>
        </Modal>
      </Container>
    </div>
  )
}