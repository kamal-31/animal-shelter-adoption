import React, { useState } from 'react'
import { Container } from '@/components/layout/Container'
import { Card } from '@/components/common/Card'
import { Badge } from '@/components/common/Badge'
import { Button } from '@/components/common/Button'
import { Modal } from '@/components/common/Modal'
import { Spinner } from '@/components/common/Spinner'
import { Alert } from '@/components/common/Alert'
import {
  useApplications,
  useApproveApplication,
  useRejectApplication,
} from '@/hooks/useAdmin'
import { usePets } from '@/hooks/usePets'
import { ApplicationStatus } from '@/types/application'
import type { AdminApplicationDto } from '@/types/application'
import { formatDate } from '@/utils/formatters'

const getStatusVariant = (status: ApplicationStatus) => {
  switch (status) {
    case 'PENDING':
      return 'warning'
    case 'APPROVED':
      return 'success'
    case 'REJECTED':
      return 'danger'
    case 'ADOPTION_CANCELLED':
      return 'default'
  }
}

export const ApplicationsPage: React.FC = () => {
  const [statusFilter, setStatusFilter] = useState<ApplicationStatus | undefined>(
    ApplicationStatus.PENDING
  )
  const [petFilter, setPetFilter] = useState<number | undefined>(undefined)
  const [selectedApp, setSelectedApp] = useState<AdminApplicationDto | null>(null)
  const [showDetailsModal, setShowDetailsModal] = useState(false)

  const { data: applications, isLoading } = useApplications({
    status: statusFilter,
    petId: petFilter,
  })
  const { data: pets } = usePets()

  const approveMutation = useApproveApplication()
  const rejectMutation = useRejectApplication()

  const handleApprove = async (id: number) => {
    if (!confirm('Are you sure you want to approve this application?')) return

    try {
      await approveMutation.mutateAsync({
        applicationId: id,
        data: { reviewedBy: 'Admin' },
      })
    } catch (error) {
      console.error('Failed to approve:', error)
      // Error will be displayed by the Alert component below
    }
  }

  const handleReject = async (id: number) => {
    if (!confirm('Are you sure you want to reject this application?')) return

    try {
      await rejectMutation.mutateAsync({
        applicationId: id,
        data: { reviewedBy: 'Admin' },
      })
    } catch (error) {
      console.error('Failed to reject:', error)
      // Error will be displayed by the Alert component below
    }
  }

  const handleViewDetails = (app: AdminApplicationDto) => {
    setSelectedApp(app)
    setShowDetailsModal(true)
  }

  return (
    <div className="min-h-screen py-8">
      <Container>
        <h1 className="text-3xl font-bold mb-6">Manage Applications</h1>

        {/* Filters */}
        <div className="flex gap-4 mb-8">
          <select
            value={statusFilter ?? ''}
            onChange={(e) =>
              setStatusFilter(
                e.target.value ? (e.target.value as ApplicationStatus) : undefined
              )
            }
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Status</option>
            <option value="PENDING">Pending</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
            <option value="ADOPTION_CANCELLED">Adoption Cancelled</option>
          </select>

          <select
            value={petFilter ?? ''}
            onChange={(e) =>
              setPetFilter(e.target.value ? Number(e.target.value) : undefined)
            }
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Pets</option>
            {pets?.map((pet) => (
              <option key={pet.id} value={pet.id}>
                {pet.name}
              </option>
            ))}
          </select>
        </div>

        {/* Loading */}
        {isLoading && (
          <div className="flex justify-center py-12">
            <Spinner size="lg" />
          </div>
        )}

        {/* Success/Error Messages */}
        {approveMutation.isSuccess && (
          <Alert
            type="success"
            message="Application approved successfully!"
            onClose={() => approveMutation.reset()}
            className="mb-4"
          />
        )}

        {/* ✅ NEW: Error message for approve */}
        {approveMutation.isError && (
          <Alert
            type="error"
            message={
              (approveMutation.error as any)?.message ||
              'Failed to approve application. Please try again.'
            }
            onClose={() => approveMutation.reset()}
            className="mb-4"
          />
        )}

        {rejectMutation.isSuccess && (
          <Alert
            type="success"
            message="Application rejected successfully!"
            onClose={() => rejectMutation.reset()}
            className="mb-4"
          />
        )}

        {/* ✅ NEW: Error message for reject */}
        {rejectMutation.isError && (
          <Alert
            type="error"
            message={
              (rejectMutation.error as any)?.message ||
              'Failed to reject application. Please try again.'
            }
            onClose={() => rejectMutation.reset()}
            className="mb-4"
          />
        )}

        {/* Applications List */}
        {applications && (
          <>
            {applications.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500 text-lg">No applications found</p>
              </div>
            ) : (
              <div className="space-y-4">
                {applications.map((app) => (
                  <Card key={app.id} className="p-4">
                    <div className="flex items-start justify-between mb-3">
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-1">
                          <h3 className="text-lg font-semibold">
                            {app.applicantName}
                          </h3>
                          <Badge variant={getStatusVariant(app.status)}>
                            {app.status}
                          </Badge>
                        </div>
                        <p className="text-sm text-gray-600">
                          {app.applicantEmail} •{' '}
                          {app.applicantPhone || 'No phone'}
                        </p>
                      </div>
                      {app.petImageUrl && (
                        <img
                          src={app.petImageUrl}
                          alt={app.petName}
                          className="w-16 h-16 object-cover rounded ml-4"
                        />
                      )}
                    </div>

                    <div className="mb-3">
                      <p className="text-sm font-medium text-gray-700 mb-1">
                        Applied for: {app.petName} ({app.petSpecies})
                      </p>
                      <p className="text-sm text-gray-600 line-clamp-2">
                        {app.reason}
                      </p>
                    </div>

                    <div className="flex items-center justify-between text-xs text-gray-500 mb-3">
                      <span>Submitted: {formatDate(app.submittedAt)}</span>
                      {app.reviewedAt && (
                        <span>Reviewed: {formatDate(app.reviewedAt)}</span>
                      )}
                    </div>

                    {app.status === 'PENDING' && (
                      <div className="flex gap-2">
                        <Button
                          variant="primary"
                          size="sm"
                          onClick={() => handleApprove(app.id)}
                          loading={approveMutation.isPending}
                          className="flex-1"
                        >
                          Approve
                        </Button>
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() => handleReject(app.id)}
                          loading={rejectMutation.isPending}
                          className="flex-1"
                        >
                          Reject
                        </Button>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleViewDetails(app)}
                        >
                          Details
                        </Button>
                      </div>
                    )}

                    {app.status !== 'PENDING' && (
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleViewDetails(app)}
                        className="w-full"
                      >
                        View Details
                      </Button>
                    )}
                  </Card>
                ))}
              </div>
            )}
          </>
        )}

        {/* Details Modal */}
        {selectedApp && (
          <Modal
            isOpen={showDetailsModal}
            onClose={() => setShowDetailsModal(false)}
            title="Application Details"
            size="lg"
          >
            <div className="space-y-4">
              <div>
                <h3 className="font-semibold text-gray-700">Applicant</h3>
                <p>{selectedApp.applicantName}</p>
                <p className="text-sm text-gray-600">
                  {selectedApp.applicantEmail}
                </p>
                <p className="text-sm text-gray-600">
                  {selectedApp.applicantPhone || 'No phone'}
                </p>
              </div>

              <div>
                <h3 className="font-semibold text-gray-700">Pet</h3>
                <p>
                  {selectedApp.petName} ({selectedApp.petSpecies})
                </p>
              </div>

              <div>
                <h3 className="font-semibold text-gray-700">
                  Reason for Adoption
                </h3>
                <p className="text-gray-800 whitespace-pre-line">
                  {selectedApp.reason}
                </p>
              </div>

              <div>
                <h3 className="font-semibold text-gray-700">Status</h3>
                <Badge variant={getStatusVariant(selectedApp.status)}>
                  {selectedApp.status}
                </Badge>
              </div>

              <div>
                <h3 className="font-semibold text-gray-700">Submitted</h3>
                <p>{formatDate(selectedApp.submittedAt)}</p>
              </div>

              {selectedApp.reviewedAt && (
                <div>
                  <h3 className="font-semibold text-gray-700">Reviewed</h3>
                  <p>{formatDate(selectedApp.reviewedAt)}</p>
                  {selectedApp.reviewedBy && (
                    <p className="text-sm text-gray-600">
                      By: {selectedApp.reviewedBy}
                    </p>
                  )}
                </div>
              )}
            </div>
          </Modal>
        )}
      </Container>
    </div>
  )
}