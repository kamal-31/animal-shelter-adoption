import React from 'react'

interface CardProps {
  children: React.ReactNode
  className?: string
  onClick?: () => void
}

export const Card: React.FC<CardProps> = ({ children, className = '', onClick }) => {
  return (
    <div
      onClick={onClick}
      className={`
        bg-white rounded-lg shadow-md
        ${onClick ? 'cursor-pointer hover:shadow-lg transition-shadow' : ''}
        ${className}
      `}
    >
      {children}
    </div>
  )
}