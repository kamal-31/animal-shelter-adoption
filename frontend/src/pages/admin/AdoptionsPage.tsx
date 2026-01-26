import React, { useState } from 'react'
import { Container } from '@/components/layout/Container'
import { Card } from '@/components/common/Card'
import { Badge } from '@/components/common/Badge'
import { Button } from '@/components/common/Button'
import { Modal } from '@/components/common/Modal'
import { Textarea } from '@/components/common/Textarea'
import { Spinner } from '@/components/common/Spinner'
import { Alert } from '@/components/common/Alert'
import {
  useAdoptions,
  useConfirmAdoption,
  useCancelAdoption,
  useReturnPet,
} from '@/hooks/useAdmin'
import { AdoptionStatus } from '@/types/adoption'
import type { AdminAdoptionDto } from '@/types/adoption'
import { formatDate } from '@/utils/formatters'

const getStatusVariant = (status: AdoptionStatus) => {
  switch (status) {
    case 'PENDING_PICKUP':
      return 'warning'
    case 'ACTIVE':
      return 'success'
    case 'RETURNED':
      return 'info'
    case 'CANCELLED':
      return 'danger'
  }
}

export const AdoptionsPage: React.FC = () => {
  const [statusFilter, setStatusFilter] = useState<AdoptionStatus | undefined>(
    undefined
  )
  const [actionModal, setActionModal] = useState<{
    type: 'confirm' | 'cancel' | 'return'
    adoption: AdminAdoptionDto
  } | null>(null)
  const [notes, setNotes] = useState('')
  const [cancelReason, setCancelReason] = useState('')
  const [returnReason, setReturnReason] = useState('')

  const { data: adoptions, isLoading } = useAdoptions({ status: statusFilter })

  const confirmMutation = useConfirmAdoption()
  const cancelMutation = useCancelAdoption()
  const returnMutation = useReturnPet()

  const handleConfirm = async () => {
    if (!actionModal) return

    try {
      await confirmMutation.mutateAsync({
        adoptionId: actionModal.adoption.id,
        data: { notes: notes || null },
      })
      setActionModal(null)
      setNotes('')
    } catch (error) {
      console.error('Failed to confirm:', error)
    }
  }

  const handleCancel = async () => {
    if (!actionModal || !cancelReason.trim()) {
      alert('Please provide a reason')
      return
    }

    try {
      await cancelMutation.mutateAsync({
        adoptionId: actionModal.adoption.id,
        data: { reason: cancelReason },
      })
      setActionModal(null)
      setCancelReason('')
    } catch (error) {
      console.error('Failed to cancel:', error)
    }
  }

  const handleReturn = async () => {
    if (!actionModal || !returnReason.trim()) {
      alert('Please provide a return reason')
      return
    }

    try {
      await returnMutation.mutateAsync({
        adoptionId: actionModal.adoption.id,
        data: {
          returnReason,
          notes: notes || null,
        },
      })
      setActionModal(null)
      setReturnReason('')
      setNotes('')
    } catch (error) {
      console.error('Failed to return:', error)
    }
  }

  return (
    <div className="min-h-screen py-8">
      <Container>
        <h1 className="text-3xl font-bold mb-6">Manage Adoptions</h1>

        {/* Filter */}
        <div className="mb-8">
          <select
            value={statusFilter ?? ''}
            onChange={(e) =>
              setStatusFilter(
                e.target.value ? (e.target.value as AdoptionStatus) : undefined
              )
            }
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Status</option>
            <option value="PENDING_PICKUP">Pending Pickup</option>
            <option value="ACTIVE">Active</option>
            <option value="RETURNED">Returned</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
        </div>

        {/* Loading */}
        {isLoading && (
          <div className="flex justify-center py-12">
            <Spinner size="lg" />
          </div>
        )}

        {/* Success Messages */}
        {confirmMutation.isSuccess && (
          <Alert
            type="success"
            message="Adoption confirmed successfully!"
            onClose={() => confirmMutation.reset()}
            className="mb-4"
          />
        )}

        {/* Adoptions List */}
        {adoptions && (
          <>
            {adoptions.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500 text-lg">No adoptions found</p>
              </div>
            ) : (
              <div className="space-y-4">
                {adoptions.map((adoption) => (
                  <Card key={adoption.id} className="p-4">
                    <div className="flex items-start justify-between mb-3">
                      <div>
                        <div className="flex items-center gap-2 mb-1">
                          <h3 className="text-lg font-semibold">
                            {adoption.petName}
                          </h3>
                          <Badge variant={getStatusVariant(adoption.status)}>
                            {adoption.status}
                          </Badge>
                        </div>
                        <p className="text-sm text-gray-600">
                          Adopted by: {adoption.applicantName}
                        </p>
                        <p className="text-sm text-gray-500">
                          {adoption.applicantEmail}
                        </p>
                      </div>
                    </div>

                    <div className="space-y-1 mb-3 text-sm">
                      <p className="text-gray-700">
                        <span className="font-medium">Adopted:</span>{' '}
                        {formatDate(adoption.adoptedAt)}
                      </p>

                      {adoption.returnedAt && (
                        <p className="text-gray-700">
                          <span className="font-medium">Returned:</span>{' '}
                          {formatDate(adoption.returnedAt)}
                        </p>
                      )}

                      {adoption.notes && (
                        <p className="text-gray-600 mt-2">
                          <span className="font-medium">Notes:</span>{' '}
                          {adoption.notes}
                        </p>
                      )}

                      {adoption.returnReason && (
                        <p className="text-gray-600 mt-2">
                          <span className="font-medium">Return Reason:</span>{' '}
                          {adoption.returnReason}
                        </p>
                      )}
                    </div>

                    {/* Actions based on status */}
                    {adoption.status === 'PENDING_PICKUP' && (
                      <div className="flex gap-2">
                        <Button
                          variant="primary"
                          size="sm"
                          onClick={() =>
                            setActionModal({ type: 'confirm', adoption })
                          }
                          className="flex-1"
                        >
                          Confirm Pickup
                        </Button>
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() =>
                            setActionModal({ type: 'cancel', adoption })
                          }
                          className="flex-1"
                        >
                          Cancel
                        </Button>
                      </div>
                    )}

                    {adoption.status === 'ACTIVE' && (
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() =>
                          setActionModal({ type: 'return', adoption })
                        }
                        className="w-full"
                      >
                        Mark as Returned
                      </Button>
                    )}
                  </Card>
                ))}
              </div>
            )}
          </>
        )}

        {/* Confirm Pickup Modal */}
        {actionModal?.type === 'confirm' && (
          <Modal
            isOpen={true}
            onClose={() => setActionModal(null)}
            title="Confirm Pet Pickup"
            footer={
              <>
                <Button variant="secondary" onClick={() => setActionModal(null)}>
                  Cancel
                </Button>
                <Button
                  variant="primary"
                  onClick={handleConfirm}
                  loading={confirmMutation.isPending}
                >
                  Confirm Pickup
                </Button>
              </>
            }
          >
            <div className="space-y-4">
              <p>Confirm that the family has picked up {actionModal.adoption.petName}?</p>
              <Textarea
                label="Notes (optional)"
                name="notes"
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                placeholder="Family was excited, provided care instructions..."
                rows={3}
              />
            </div>
          </Modal>
        )}

        {/* Cancel Adoption Modal */}
        {actionModal?.type === 'cancel' && (
          <Modal
            isOpen={true}
            onClose={() => setActionModal(null)}
            title="Cancel Adoption"
            footer={
              <>
                <Button variant="secondary" onClick={() => setActionModal(null)}>
                  Cancel
                </Button>
                <Button
                  variant="danger"
                  onClick={handleCancel}
                  loading={cancelMutation.isPending}
                >
                  Cancel Adoption
                </Button>
              </>
            }
          >
            <Textarea
              label="Reason"
              name="cancelReason"
              value={cancelReason}
              onChange={(e) => setCancelReason(e.target.value)}
              placeholder="Family didn't show up, no response to calls..."
              rows={3}
              required
            />
          </Modal>
        )}

        {/* Return Pet Modal */}
        {actionModal?.type === 'return' && (
          <Modal
            isOpen={true}
            onClose={() => setActionModal(null)}
            title="Mark Pet as Returned"
            footer={
              <>
                <Button variant="secondary" onClick={() => setActionModal(null)}>
                  Cancel
                </Button>
                <Button
                  variant="primary"
                  onClick={handleReturn}
                  loading={returnMutation.isPending}
                >
                  Mark as Returned
                </Button>
              </>
            }
          >
            <div className="space-y-4">
              <Textarea
                label="Return Reason"
                name="returnReason"
                value={returnReason}
                onChange={(e) => setReturnReason(e.target.value)}
                placeholder="Owner developed allergies, pet returned in good health..."
                rows={3}
                required
              />
              <Textarea
                label="Additional Notes (optional)"
                name="notes"
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                placeholder="Pet is healthy and ready for re-adoption"
                rows={3}
              />
            </div>
          </Modal>
        )}
      </Container>
    </div>
  )
}