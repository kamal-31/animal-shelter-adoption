import React from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { Header } from '@/components/layout/Header'
import { Footer } from '@/components/layout/Footer'
import { HomePage } from '@/pages/public/HomePage'
import { PetListPage } from '@/pages/public/PetListPage'
import { PetDetailsPage } from '@/pages/public/PetDetailsPage'
import { AdminDashboard } from '@/pages/admin/AdminDashboard'
import { ApplicationsPage } from '@/pages/admin/ApplicationsPage'
import { AdoptionsPage } from '@/pages/admin/AdoptionsPage'
import { ManagePetsPage } from '@/pages/admin/ManagePetsPage'

function App() {
  return (
    <BrowserRouter>
      <div className="flex flex-col min-h-screen">
        <Header />
        <main className="flex-1">
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<HomePage />} />
            <Route path="/pets" element={<PetListPage />} />
            <Route path="/pets/:id" element={<PetDetailsPage />} />

            {/* Admin Routes */}
            <Route path="/admin" element={<AdminDashboard />} />
            <Route path="/admin/applications" element={<ApplicationsPage />} />
            <Route path="/admin/adoptions" element={<AdoptionsPage />} />
            <Route path="/admin/pets" element={<ManagePetsPage />} />
          </Routes>
        </main>
        <Footer />
      </div>
    </BrowserRouter>
  )
}

export default App