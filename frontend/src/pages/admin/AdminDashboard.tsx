import React from 'react'
import { useNavigate } from 'react-router-dom'
import { Container } from '@/components/layout/Container'
import { Card } from '@/components/common/Card'
import { Button } from '@/components/common/Button'
import { usePets } from '@/hooks/usePets'
import { useApplications, useAdoptions } from '@/hooks/useAdmin'
import { PetStatus } from '@/types/pet'
import { ApplicationStatus } from '@/types/application'
import { AdoptionStatus } from '@/types/adoption'

export const AdminDashboard: React.FC = () => {
  const navigate = useNavigate()

  const { data: availablePets } = usePets(PetStatus.AVAILABLE)
  const { data: pendingApplications } = useApplications({
    status: ApplicationStatus.PENDING,
  })
  const { data: activeAdoptions } = useAdoptions({
    status: AdoptionStatus.ACTIVE,
  })
  const { data: pendingPickups } = useAdoptions({
    status: AdoptionStatus.PENDING_PICKUP,
  })

  return (
    <div className="min-h-screen py-8">
      <Container>
        <h1 className="text-3xl font-bold mb-8">Admin Dashboard</h1>

        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-12">
          <Card className="p-6">
            <h3 className="text-sm font-medium text-gray-600 mb-2">
              Available Pets
            </h3>
            <p className="text-3xl font-bold text-gray-900">
              {availablePets?.length || 0}
            </p>
          </Card>

          <Card className="p-6">
            <h3 className="text-sm font-medium text-gray-600 mb-2">
              Pending Applications
            </h3>
            <p className="text-3xl font-bold text-gray-900">
              {pendingApplications?.length || 0}
            </p>
          </Card>

          <Card className="p-6">
            <h3 className="text-sm font-medium text-gray-600 mb-2">
              Active Adoptions
            </h3>
            <p className="text-3xl font-bold text-gray-900">
              {activeAdoptions?.length || 0}
            </p>
          </Card>

          <Card className="p-6">
            <h3 className="text-sm font-medium text-gray-600 mb-2">
              Pending Pickups
            </h3>
            <p className="text-3xl font-bold text-gray-900">
              {pendingPickups?.length || 0}
            </p>
          </Card>
        </div>

        {/* Quick Actions */}
        <section className="mb-12">
          <h2 className="text-2xl font-bold mb-4">Quick Actions</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Button
              variant="primary"
              onClick={() => navigate('/admin/applications')}
              className="h-20 text-lg"
            >
              📋 View Applications
            </Button>
            <Button
              variant="primary"
              onClick={() => navigate('/admin/adoptions')}
              className="h-20 text-lg"
            >
              🏠 View Adoptions
            </Button>
            <Button
              variant="primary"
              onClick={() => navigate('/admin/pets')}
              className="h-20 text-lg"
            >
              🐾 Manage Pets
            </Button>
          </div>
        </section>

        {/* Recent Applications Preview */}
        {pendingApplications && pendingApplications.length > 0 && (
          <section>
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-2xl font-bold">Recent Applications</h2>
              <Button
                variant="outline"
                size="sm"
                onClick={() => navigate('/admin/applications')}
              >
                View All
              </Button>
            </div>
            <div className="space-y-4">
              {pendingApplications.slice(0, 5).map((app) => (
                <Card key={app.id} className="p-4">
                  <div className="flex items-center justify-between">
                    <div>
                      <h3 className="font-semibold">{app.applicantName}</h3>
                      <p className="text-sm text-gray-600">
                        Applied for: {app.petName} ({app.petSpecies})
                      </p>
                    </div>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => navigate('/admin/applications')}
                    >
                      Review
                    </Button>
                  </div>
                </Card>
              ))}
            </div>
          </section>
        )}
      </Container>
    </div>
  )
}