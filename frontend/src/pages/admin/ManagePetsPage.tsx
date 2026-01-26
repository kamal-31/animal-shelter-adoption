import React, { useState } from 'react'
import { Container } from '@/components/layout/Container'
import { Card } from '@/components/common/Card'
import { Badge } from '@/components/common/Badge'
import { Button } from '@/components/common/Button'
import { Modal } from '@/components/common/Modal'
import { Input } from '@/components/common/Input'
import { Textarea } from '@/components/common/Textarea'
import { Spinner } from '@/components/common/Spinner'
import { Alert } from '@/components/common/Alert'
import {
  useAdminPets,
  useCreatePet,
  useUpdatePet,
  useDeletePet,
  useUploadImage,
} from '@/hooks/useAdmin'
import { useSpecies } from '@/hooks/usePets'
import type { Pet, CreatePetRequest, UpdatePetRequest } from '@/types/pet'
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

export const ManagePetsPage: React.FC = () => {
  const [includeDeleted, setIncludeDeleted] = useState(false)
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [editingPet, setEditingPet] = useState<Pet | null>(null)
  const [imageFile, setImageFile] = useState<File | null>(null)
  const [imagePreview, setImagePreview] = useState<string | null>(null)

  const { data: pets, isLoading } = useAdminPets(includeDeleted)
  const { data: species } = useSpecies()

  const createMutation = useCreatePet()
  const updateMutation = useUpdatePet()
  const deleteMutation = useDeletePet()
  const uploadMutation = useUploadImage()

  const [formData, setFormData] = useState<CreatePetRequest>({
    name: '',
    speciesId: 0,
    age: 0,
    imageUrl: null,
    description: null,
  })

  const [errors, setErrors] = useState<Record<string, string>>({})
  const [submitError, setSubmitError] = useState<string | null>(null)
  const [deleteError, setDeleteError] = useState<string | null>(null)

  const resetForm = () => {
    setFormData({
      name: '',
      speciesId: 0,
      age: 0,
      imageUrl: null,
      description: null,
    })
    setImageFile(null)
    setImagePreview(null)
    setErrors({})
    setSubmitError(null)
  }

  const handleOpenCreate = () => {
    resetForm()
    setShowCreateModal(true)
    setEditingPet(null)
  }

  const handleOpenEdit = (pet: Pet) => {
    setFormData({
      name: pet.name,
      speciesId: species?.find((s) => s.name === pet.species)?.id || 0,
      age: pet.age,
      imageUrl: pet.imageUrl,
      description: pet.description,
    })
    setImagePreview(pet.imageUrl)
    setEditingPet(pet)
    setShowCreateModal(true)
  }

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      setImageFile(file)
      const reader = new FileReader()
      reader.onloadend = () => {
        setImagePreview(reader.result as string)
      }
      reader.readAsDataURL(file)
    }
  }

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {}

    if (!formData.name.trim()) {
      newErrors.name = 'Name is required'
    }

    if (!formData.speciesId || formData.speciesId === 0) {
      newErrors.speciesId = 'Species is required'
    }

    if (formData.age < 0) {
      newErrors.age = 'Age must be 0 or positive'
    }

    if (formData.age > 50) {
      newErrors.age = 'Age must be less than 50'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async () => {
    if (!validate()) return

    try {
      let imageUrl = formData.imageUrl

      // Upload image if selected
      if (imageFile) {
        const uploadResult = await uploadMutation.mutateAsync(imageFile)
        imageUrl = uploadResult.imageUrl
      }

      if (editingPet) {
        // Update existing pet
        const updateData: UpdatePetRequest = {
          name: formData.name !== editingPet.name ? formData.name : undefined,
          age: formData.age !== editingPet.age ? formData.age : undefined,
          imageUrl: imageUrl !== editingPet.imageUrl ? imageUrl : undefined,
          description:
            formData.description !== editingPet.description
              ? formData.description
              : undefined,
        }

        // Only send if there are changes
        if (Object.values(updateData).some((val) => val !== undefined)) {
          await updateMutation.mutateAsync({
            petId: editingPet.id,
            data: updateData,
          })
        }
      } else {
        // Create new pet
        await createMutation.mutateAsync({
          ...formData,
          imageUrl,
        })
      }

      setShowCreateModal(false)
      resetForm()
      setEditingPet(null)
    } catch (error: any) {
      console.error('Failed to save pet:', error)
      if (error.response?.data?.details) {
        setErrors(error.response.data.details)
      }
      const errorMessage = error.response?.data?.message || error.message || 'An unexpected error occurred'
      setSubmitError(errorMessage)
    }
  }

  const handleDelete = async (petId: number, petName: string) => {
    if (!confirm(`Are you sure you want to delete ${petName}?`)) return

    setDeleteError(null)
    try {
      await deleteMutation.mutateAsync(petId)
    } catch (error: any) {
      console.error('Failed to delete pet:', error)
      const errorMessage = error.response?.data?.message || error.message || 'Failed to delete pet'
      setDeleteError(errorMessage)
    }
  }

  return (
    <div className="min-h-screen py-8">
      <Container>
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-3xl font-bold">Manage Pets</h1>
          <Button variant="primary" onClick={handleOpenCreate}>
            + Add New Pet
          </Button>
        </div>

        {/* Toggle deleted */}
        <div className="mb-6">
          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              checked={includeDeleted}
              onChange={(e) => setIncludeDeleted(e.target.checked)}
              className="w-4 h-4"
            />
            <span className="text-sm text-gray-700">Include deleted pets</span>
          </label>
        </div>

        {/* Alerts */}
        <div className="space-y-4 mb-6">
          {deleteError && (
            <Alert
              type="error"
              message={deleteError}
              onClose={() => setDeleteError(null)}
            />
          )}
          {createMutation.isSuccess && (
            <Alert
              type="success"
              message="Pet created successfully!"
              onClose={() => createMutation.reset()}
            />
          )}
          {updateMutation.isSuccess && (
            <Alert
              type="success"
              message="Pet updated successfully!"
              onClose={() => updateMutation.reset()}
            />
          )}
          {deleteMutation.isSuccess && (
            <Alert
              type="success"
              message="Pet deleted successfully!"
              onClose={() => deleteMutation.reset()}
            />
          )}
        </div>

        {/* Loading */}
        {isLoading && (
          <div className="flex justify-center py-12">
            <Spinner size="lg" />
          </div>
        )}

        {/* Pets List */}
        {pets && (
          <>
            {pets.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500 text-lg">No pets found</p>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {pets.map((pet) => (
                  <Card key={pet.id} className="overflow-hidden">
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
                      <h3 className="text-lg font-semibold mb-1">{pet.name}</h3>
                      <p className="text-sm text-gray-600 mb-2">
                        {pet.species} • {pet.age} {pet.age === 1 ? 'year' : 'years'}{' '}
                        old
                      </p>

                      {pet.description && (
                        <p className="text-sm text-gray-700 line-clamp-2 mb-3">
                          {pet.description}
                        </p>
                      )}

                      {pet.pendingApplicationCount > 0 && (
                        <p className="text-xs text-gray-500 mb-3">
                          {pet.pendingApplicationCount} pending application(s)
                        </p>
                      )}

                      {pet.deletedAt ? (
                        <p className="text-sm text-red-500 italic">Deleted</p>
                      ) : (
                        <div className="flex gap-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => handleOpenEdit(pet)}
                            className="flex-1"
                          >
                            Edit
                          </Button>
                          <Button
                            variant="danger"
                            size="sm"
                            onClick={() => handleDelete(pet.id, pet.name)}
                            loading={deleteMutation.isPending}
                            className="flex-1"
                          >
                            Delete
                          </Button>
                        </div>
                      )}
                    </div>
                  </Card>
                ))}
              </div>
            )}
          </>
        )}

        {/* Create/Edit Modal */}
        <Modal
          isOpen={showCreateModal}
          onClose={() => {
            setShowCreateModal(false)
            resetForm()
            setEditingPet(null)
          }}
          title={editingPet ? 'Edit Pet' : 'Add New Pet'}
          size="lg"
          footer={
            <>
              <Button
                variant="secondary"
                onClick={() => {
                  setShowCreateModal(false)
                  resetForm()
                  setEditingPet(null)
                }}
              >
                Cancel
              </Button>
              <Button
                variant="primary"
                onClick={handleSubmit}
                loading={
                  createMutation.isPending ||
                  updateMutation.isPending ||
                  uploadMutation.isPending
                }
              >
                {editingPet ? 'Update Pet' : 'Create Pet'}
              </Button>
            </>
          }
        >
          <div className="space-y-4">
            {submitError && (
              <Alert
                type="error"
                message={submitError}
                onClose={() => setSubmitError(null)}
              />
            )}
            <Input
              label="Name"
              type="text"
              name="name"
              value={formData.name}
              onChange={(e) =>
                setFormData({ ...formData, name: e.target.value })
              }
              error={errors.name}
              required
              placeholder="Buddy"
            />

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Species <span className="text-red-500">*</span>
              </label>
              <select
                value={formData.speciesId}
                onChange={(e) =>
                  setFormData({ ...formData, speciesId: Number(e.target.value) })
                }
                className={`
                  w-full px-3 py-2 border rounded-md
                  focus:outline-none focus:ring-2 focus:ring-blue-500
                  ${errors.speciesId ? 'border-red-500' : 'border-gray-300'}
                `}
              >
                <option value={0}>Select species...</option>
                {species?.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.name}
                  </option>
                ))}
              </select>
              {errors.speciesId && (
                <p className="mt-1 text-sm text-red-500">{errors.speciesId}</p>
              )}
            </div>

            <Input
              label="Age"
              type="number"
              name="age"
              value={formData.age.toString()}
              onChange={(e) =>
                setFormData({ ...formData, age: Number(e.target.value) })
              }
              error={errors.age}
              required
              placeholder="3"
            />

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Image
              </label>
              <input
                type="file"
                accept="image/*"
                onChange={handleImageChange}
                className="w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
              />
              {imagePreview && (
                <img
                  src={imagePreview}
                  alt="Preview"
                  className="mt-2 w-32 h-32 object-cover rounded"
                />
              )}
            </div>

            <Textarea
              label="Description"
              name="description"
              value={formData.description || ''}
              onChange={(e) =>
                setFormData({ ...formData, description: e.target.value })
              }
              placeholder="Friendly, loves to play, good with kids..."
              rows={4}
            />
          </div>
        </Modal>
      </Container>
    </div>
  )
}