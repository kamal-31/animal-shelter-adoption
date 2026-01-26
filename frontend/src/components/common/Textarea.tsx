import React from 'react'

interface TextareaProps {
  label: string
  name: string
  value: string
  onChange: (e: React.ChangeEvent<HTMLTextAreaElement>) => void
  placeholder?: string
  rows?: number
  error?: string
  required?: boolean
  disabled?: boolean
  maxLength?: number
  className?: string
}

export const Textarea: React.FC<TextareaProps> = ({
  label,
  name,
  value,
  onChange,
  placeholder,
  rows = 4,
  error,
  required = false,
  disabled = false,
  maxLength,
  className = '',
}) => {
  return (
    <div className={`mb-4 ${className}`}>
      <label
        htmlFor={name}
        className="block text-sm font-medium text-gray-700 mb-1"
      >
        {label}
        {required && <span className="text-red-500 ml-1">*</span>}
      </label>

      <textarea
        id={name}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        rows={rows}
        disabled={disabled}
        maxLength={maxLength}
        className={`
          w-full px-3 py-2 border rounded-md
          focus:outline-none focus:ring-2 focus:ring-blue-500
          ${error ? 'border-red-500' : 'border-gray-300'}
          ${disabled ? 'bg-gray-100 cursor-not-allowed' : 'bg-white'}
        `}
      />

      <div className="flex justify-between mt-1">
        {error && <p className="text-sm text-red-500">{error}</p>}
        {maxLength && (
          <p className="text-sm text-gray-500 ml-auto">
            {value.length} / {maxLength}
          </p>
        )}
      </div>
    </div>
  )
}