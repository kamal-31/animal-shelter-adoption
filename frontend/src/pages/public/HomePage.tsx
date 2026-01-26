import React from 'react'
import { useNavigate } from 'react-router-dom'
import { Container } from '@/components/layout/Container'
import { Button } from '@/components/common/Button'
import { PetGrid } from '@/components/pets/PetGrid'
import { Spinner } from '@/components/common/Spinner'
import { usePets } from '@/hooks/usePets'
import { PetStatus } from '@/types/pet'

export const HomePage: React.FC = () => {
  const navigate = useNavigate()
  const { data: pets, isLoading } = usePets(PetStatus.AVAILABLE)

  // Show first 6 pets as featured
  const featuredPets = pets?.slice(0, 6) || []

  return (
    <div className="min-h-screen flex flex-col">
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-blue-500 to-purple-600 text-white py-20">
        <Container>
          <div className="text-center max-w-3xl mx-auto">
            <h1 className="text-5xl font-bold mb-4">
              Find Your Perfect Companion
            </h1>
            <p className="text-xl mb-8">
              Give a loving home to a pet in need. Browse our available pets
              and start your adoption journey today.
            </p>
          </div>
        </Container>
      </section>

      {/* Featured Pets */}
      <section className="py-16 bg-gray-50">
        <Container>
          <h2 className="text-3xl font-bold text-center mb-8">Featured Pets</h2>

          {isLoading && (
            <div className="flex justify-center py-12">
              <Spinner size="lg" />
            </div>
          )}

          {featuredPets.length > 0 && <PetGrid pets={featuredPets} />}

          <div className="text-center mt-8">
            <Button variant="outline" onClick={() => navigate('/pets')}>
              View All Pets
            </Button>
          </div>
        </Container>
      </section>

      {/* How It Works */}
      <section className="py-16">
        <Container>
          <h2 className="text-3xl font-bold text-center mb-12">
            How Adoption Works
          </h2>

          <div className="grid md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="w-16 h-16 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center text-2xl font-bold mx-auto mb-4">
                1
              </div>
              <h3 className="text-xl font-semibold mb-2">Browse Pets</h3>
              <p className="text-gray-600">
                Explore our available pets and find your perfect match
              </p>
            </div>

            <div className="text-center">
              <div className="w-16 h-16 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center text-2xl font-bold mx-auto mb-4">
                2
              </div>
              <h3 className="text-xl font-semibold mb-2">Submit Application</h3>
              <p className="text-gray-600">
                Fill out a simple adoption application form
              </p>
            </div>

            <div className="text-center">
              <div className="w-16 h-16 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center text-2xl font-bold mx-auto mb-4">
                3
              </div>
              <h3 className="text-xl font-semibold mb-2">Welcome Home</h3>
              <p className="text-gray-600">
                Once approved, schedule a pickup and bring your new friend home
              </p>
            </div>
          </div>
        </Container>
      </section>

      {/* CTA */}
      <section className="py-16 bg-blue-600 text-white">
        <Container>
          <div className="text-center">
            <h2 className="text-3xl font-bold mb-4">Ready to Change a Life?</h2>
            <p className="text-xl mb-8">Start your adoption journey today</p>
            <Button
              variant="secondary"
              size="lg"
              onClick={() => navigate('/pets')}
            >
              View All Available Pets
            </Button>
          </div>
        </Container>
      </section>
    </div>
  )
}