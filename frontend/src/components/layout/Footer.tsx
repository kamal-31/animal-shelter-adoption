import React from 'react'
import { Container } from './Container'

export const Footer: React.FC = () => {
  return (
    <footer className="bg-gray-800 text-white py-8 mt-auto">
      <Container>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* About */}
          <div>
            <h3 className="text-lg font-semibold mb-3">About Us</h3>
            <p className="text-gray-300 text-sm">
              We're dedicated to finding loving homes for animals in need.
              Every pet deserves a second chance at happiness.
            </p>
          </div>

          {/* Contact */}
          <div>
            <h3 className="text-lg font-semibold mb-3">Contact</h3>
            <ul className="text-gray-300 text-sm space-y-2">
              <li>Email: info@animalshelter.com</li>
              <li>Phone: (555) 123-4567</li>
              <li>Address: 123 Shelter St, City, State</li>
            </ul>
          </div>

          {/* Quick Links */}
          <div>
            <h3 className="text-lg font-semibold mb-3">Quick Links</h3>
            <ul className="text-gray-300 text-sm space-y-2">
              <li>
                <a href="/pets" className="hover:text-white transition-colors">
                  Available Pets
                </a>
              </li>
              <li>
                <a href="/admin" className="hover:text-white transition-colors">
                  Admin Portal
                </a>
              </li>
            </ul>
          </div>
        </div>

        {/* Copyright */}
        <div className="mt-8 pt-8 border-t border-gray-700 text-center text-sm text-gray-400">
          © {new Date().getFullYear()} Animal Shelter. All rights reserved.
        </div>
      </Container>
    </footer>
  )
}
