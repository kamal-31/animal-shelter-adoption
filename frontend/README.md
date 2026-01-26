# Animal Shelter Frontend

React + TypeScript + Vite frontend for the Animal Shelter Adoption Portal.

## Tech Stack

- **Framework:** React 18
- **Language:** TypeScript
- **Build Tool:** Vite
- **Routing:** React Router v6
- **State Management:** TanStack Query (React Query)
- **HTTP Client:** Axios
- **Styling:** Tailwind CSS
- **Forms:** React Hook Form

## Prerequisites

- Node.js 18+
- npm or yarn

## Getting Started

### 1. Install Dependencies
```bash
npm install
```

### 2. Start Development Server
```bash
npm run dev
```

Application starts on `http://localhost:5173`

### 3. Build for Production
```bash
npm run build
```

### 4. Preview Production Build
```bash
npm run preview
```

## Project Structure
```
src/
├── api/              # API client and service functions
├── components/       # Reusable components
│   ├── common/      # Generic UI components
│   ├── layout/      # Layout components (Header, Footer)
│   └── pets/        # Pet-specific components
├── hooks/           # Custom React hooks
├── pages/           # Page components
│   ├── public/      # Public-facing pages
│   └── admin/       # Admin pages
├── types/           # TypeScript type definitions
├── utils/           # Utility functions
├── App.tsx          # Main app component with routing
├── main.tsx         # Application entry point
└── index.css        # Global styles
```

## Features

### Public Features
- Browse available pets
- View pet details
- Submit adoption applications
- Filter by species and status

### Admin Features
- Dashboard with statistics
- Review and approve/reject applications
- Manage adoption lifecycle (pickup, return, cancel)
- Create, update, and delete pets
- Upload pet images

## Environment Variables

Create a `.env` file:
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## API Integration

The app connects to the Spring Boot backend API at `http://localhost:8080/api`

API endpoints are organized in `src/api/`:
- `pets.ts` - Public pet endpoints
- `applications.ts` - Application submission
- `admin.ts` - Admin operations

## Styling

Using Tailwind CSS for styling. Configuration in `tailwind.config.js`

Common components provide consistent UI:
- Button, Input, Textarea
- Card, Badge, Alert
- Modal, Spinner

## State Management

Using TanStack Query for server state:
- Automatic caching
- Background refetching
- Optimistic updates
- Error handling

Custom hooks in `src/hooks/` encapsulate all API calls.
```

---

## ✅ **FRONTEND COMPLETE!**

**Final files generated (5 files):**

**Admin Pages:**
42. ✅ src/pages/admin/AdminDashboard.tsx
43. ✅ src/pages/admin/ApplicationsPage.tsx
44. ✅ src/pages/admin/AdoptionsPage.tsx
45. ✅ src/pages/admin/ManagePetsPage.tsx

**App Router:**
46. ✅ src/App.tsx

**Documentation:**
47. ✅ README.md

---

## **Complete Frontend Structure**
```
frontend/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── tailwind.config.js
├── index.html
├── .env
├── .gitignore
├── README.md
└── src/
    ├── main.tsx
    ├── App.tsx
    ├── index.css
    ├── vite-env.d.ts
    ├── api/
    │   ├── client.ts
    │   ├── pets.ts
    │   ├── applications.ts
    │   └── admin.ts
    ├── hooks/
    │   ├── usePets.ts
    │   ├── useApplications.ts
    │   └── useAdmin.ts
    ├── types/
    │   ├── pet.ts
    │   ├── application.ts
    │   ├── adoption.ts
    │   └── api.ts
    ├── utils/
    │   └── formatters.ts
    ├── components/
    │   ├── common/
    │   │   ├── Button.tsx
    │   │   ├── Spinner.tsx
    │   │   ├── Card.tsx
    │   │   ├── Alert.tsx
    │   │   ├── Badge.tsx
    │   │   ├── Modal.tsx
    │   │   ├── Input.tsx
    │   │   └── Textarea.tsx
    │   ├── layout/
    │   │   ├── Container.tsx
    │   │   ├── Header.tsx
    │   │   └── Footer.tsx
    │   └── pets/
    │       ├── PetCard.tsx
    │       ├── PetGrid.tsx
    │       └── ApplicationForm.tsx
    └── pages/
        ├── public/
        │   ├── HomePage.tsx
        │   ├── PetListPage.tsx
        │   └── PetDetailsPage.tsx
        └── admin/
            ├── AdminDashboard.tsx
            ├── ApplicationsPage.tsx
            ├── AdoptionsPage.tsx
            └── ManagePetsPage.tsx