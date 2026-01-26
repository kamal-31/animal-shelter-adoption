import apiClient from './client'
import type {
  SubmitApplicationRequest,
  SubmitApplicationResponse,
} from '@/types/application'

/**
 * Submit adoption application
 */
export const submitApplication = async (
  data: SubmitApplicationRequest
): Promise<SubmitApplicationResponse> => {
  const response = await apiClient.post<SubmitApplicationResponse>(
    '/applications',
    data
  )
  return response.data
}