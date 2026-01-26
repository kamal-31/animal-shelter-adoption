import { useMutation, useQueryClient } from '@tanstack/react-query'
import { submitApplication } from '@/api/applications'
import type { SubmitApplicationRequest } from '@/types/application'

/**
 * Submit adoption application
 */
export const useSubmitApplication = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: SubmitApplicationRequest) => submitApplication(data),
    onSuccess: (_, variables) => {
      // Invalidate pet query to update application count
      queryClient.invalidateQueries({ queryKey: ['pets', variables.petId] })
      queryClient.invalidateQueries({ queryKey: ['pets'] })
    },
  })
}