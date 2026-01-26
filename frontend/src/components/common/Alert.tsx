import React from 'react'

interface AlertProps {
  type: 'success' | 'error' | 'warning' | 'info'
  message: string
  onClose?: () => void
  className?: string
}

export const Alert: React.FC<AlertProps> = ({ type, message, onClose, className }) => {
  const typeStyles = {
    success: 'bg-green-50 text-green-800 border-green-200',
    error: 'bg-red-50 text-red-800 border-red-200',
    warning: 'bg-yellow-50 text-yellow-800 border-yellow-200',
    info: 'bg-blue-50 text-blue-800 border-blue-200',
  }

  return (
    <div
      className={`
        flex items-center justify-between
        px-4 py-3 rounded border
        ${typeStyles[type]}
        ${className || ''}
      `}
    >
      <p>{message}</p>
      {onClose && (
        <button
          onClick={onClose}
          className="ml-4 font-bold hover:opacity-70 transition-opacity"
        >
          ✕
        </button>
      )}
    </div>
  )
}