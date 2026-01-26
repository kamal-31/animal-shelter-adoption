import React from 'react'
import { Link, useLocation } from 'react-router-dom'
import { Container } from './Container'

export const Header: React.FC = () => {
  const location = useLocation()

  const isActive = (path: string) => location.pathname === path

  return (
    <header className="bg-white shadow-sm sticky top-0 z-40">
      <Container>
        <div className="flex justify-between items-center py-4">
          {/* Logo */}
          <Link to="/" className="flex items-center">
            <span className="text-2xl font-bold text-blue-600">
              🐾 Animal Shelter
            </span>
          </Link>

          {/* Navigation */}
          <nav className="flex items-center gap-6">
            <Link
              to="/"
              className={`
                text-base font-medium transition-colors
                ${
                  isActive('/')
                    ? 'text-blue-600'
                    : 'text-gray-700 hover:text-blue-600'
                }
              `}
            >
              Home
            </Link>
            <Link
              to="/pets"
              className={`
                text-base font-medium transition-colors
                ${
                  isActive('/pets')
                    ? 'text-blue-600'
                    : 'text-gray-700 hover:text-blue-600'
                }
              `}
            >
              Browse Pets
            </Link>
            <Link
              to="/admin"
              className={`
                text-base font-medium transition-colors
                ${
                  isActive('/admin') || location.pathname.startsWith('/admin')
                    ? 'text-blue-600'
                    : 'text-gray-700 hover:text-blue-600'
                }
              `}
            >
              Admin
            </Link>
          </nav>
        </div>
      </Container>
    </header>
  )
}