import React, { useState } from 'react'
import { Card } from '@/components/common/Card'
import { Input } from '@/components/common/Input'
import { Textarea } from '@/components/common/Textarea'
import { Button } from '@/components/common/Button'
import { Alert } from '@/components/common/Alert'
import { useSubmitApplication } from '@/hooks/useApplications'
import type { SubmitApplicationRequest } from '@/types/application'

interface ApplicationFormProps {
  petId: number
  petName: string
  onSuccess: () => void
}

export const ApplicationForm: React.FC<ApplicationFormProps> = ({
  petId,
  petName,
  onSuccess,
}) => {
  const [formData, setFormData] = useState({
    applicantName: '',
    email: '',
    phone: '',
    reason: '',
  })

  const [errors, setErrors] = useState<Record<string, string>>({})
  const submitApplication = useSubmitApplication()

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
    // Clear error when user starts typing
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }))
    }
  }

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {}

    if (!formData.applicantName.trim()) {
      newErrors.applicantName = 'Name is required'
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email is required'
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'Invalid email format'
    }

    if (!formData.reason.trim()) {
      newErrors.reason = 'Please tell us why you want to adopt'
    } else if (formData.reason.length < 50) {
      newErrors.reason = 'Please provide at least 50 characters'
    } else if (formData.reason.length > 5000) {
      newErrors.reason = 'Maximum 5000 characters'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!validate()) return

    const request: SubmitApplicationRequest = {
      petId,
      applicantName: formData.applicantName,
      email: formData.email,
      phone: formData.phone || null,
      reason: formData.reason,
    }

    try {
      await submitApplication.mutateAsync(request)
      onSuccess()
    } catch (error: any) {
      if (error.response?.data?.details) {
        setErrors(error.response.data.details)
      }
    }
  }

  return (
    <Card className="p-6">
      <h2 className="text-2xl font-bold mb-4">Apply to Adopt {petName}</h2>

      {submitApplication.isError && (
        <Alert
          type="error"
          message={
            submitApplication.error?.message ||
            'Failed to submit application. Please try again.'
          }
          onClose={() => submitApplication.reset()}
          className="mb-4"
        />
      )}

      <form onSubmit={handleSubmit}>
        <Input
          label="Your Name"
          type="text"
          name="applicantName"
          value={formData.applicantName}
          onChange={handleChange}
          error={errors.applicantName}
          required
          placeholder="John Doe"
        />

        <Input
          label="Email Address"
          type="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          error={errors.email}
          required
          placeholder="john@example.com"
        />

        <Input
          label="Phone Number"
          type="tel"
          name="phone"
          value={formData.phone}
          onChange={handleChange}
          error={errors.phone}
          placeholder="555-0101 (optional)"
        />

        <Textarea
          label="Why do you want to adopt this pet?"
          name="reason"
          value={formData.reason}
          onChange={handleChange}
          error={errors.reason}
          required
          rows={6}
          maxLength={5000}
          placeholder="Tell us about your home, experience with pets, and why you'd be a great match..."
        />

        <Button
          type="submit"
          variant="primary"
          loading={submitApplication.isPending}
          className="w-full"
        >
          Submit Application
        </Button>
      </form>
    </Card>
  )
}