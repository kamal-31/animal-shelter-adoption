import axios from 'axios'
import type { ErrorResponse } from '@/types/api'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.data) {
      const errorData = error.response.data as ErrorResponse
      // Attach formatted error to the error object
      error.message = errorData.message || error.message
      error.details = errorData.details
    }
    return Promise.reject(error)
  }
)

export default apiClient