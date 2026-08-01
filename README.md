# Insurance Management Platform

A full-stack web application for managing insurance operations — policies, claims, premium payments, customer records, and documents — with role-based access for Admins, Agents, and Customers.

**Live Demo:** https://insurance-management-platform-six.vercel.app/login

## Demo Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@test.com | pass123 |
| Customer | customer@test.com | pass123 |

> Note: The backend is hosted on Render's free tier, which spins down after inactivity. The first request may take 30-60 seconds while the server wakes up.

## Overview

The Insurance Management Platform digitizes the end-to-end insurance workflow — from customer onboarding and policy creation, to premium tracking, claim submission, review, and settlement — backed by a secure REST API and a responsive React frontend.

## Tech Stack

**Backend**
- Java 21, Spring Boot 3
- Spring Security + JWT authentication
- Spring Data JPA (Hibernate)
- PostgreSQL (hosted on Supabase)
- Maven
- BCrypt password hashing
- Jakarta Bean Validation

**Frontend**
- React (Vite)
- React Router
- Axios
- Chart.js (reports dashboard)
- Custom responsive CSS

**Deployment**
- Backend: Render (Docker)
- Frontend: Vercel
- Database: Supabase (PostgreSQL)

## Features

### Customer Management
- Self-service profile creation and viewing
- Admin/Agent view of all customer records

### Policy Management
- Create, view, and update insurance policies
- Role-based access: Admins and Agents manage policies; Customers view their own
- Search, filter, and pagination support

### Claim Management
- Customers file claims against their policies
- Agents/Admins review, approve, reject, or settle claims
- Automatic validation against policy coverage limits

### Premium Tracking
- Record premium payments with due dates
- Settle payments with method tracking
- Automatic overdue detection

### Document Management
- Upload and download policy/claim/ID documents
- File storage with metadata tracking

### Reports Dashboard
- Real-time aggregated statistics (active policies, claims by status, premium collected)
- Visual charts (Doughnut and Bar) via Chart.js

### Security
- JWT-based stateless authentication
- Role-based authorization (\@PreAuthorize\) across all endpoints
- BCrypt password hashing
- Centralized exception handling with clean, safe error responses

## User Roles

| Role | Capabilities |
|------|-------------|
| **Admin** | Full access: manage customers, policies, claims, payments; delete records; view reports |
| **Agent** | Create/manage policies, review claims, manage payments; view reports |
| **Customer** | View own policies/claims/payments, file claims, upload documents, manage own profile |

## Project Structure

\\\
insurance-management-platform/
├── src/main/java/com/insurance/management/
│   ├── controller/     REST endpoints
│   ├── service/        Business logic
│   ├── repository/     Data access (Spring Data JPA)
│   ├── entity/         Database models
│   ├── dto/            Request/response objects
│   ├── security/       JWT filter and utilities
│   ├── config/         Security and CORS configuration
│   └── exception/      Global exception handling
├── frontend/
│   └── src/
│       ├── pages/       Route-level components
│       ├── components/  Shared UI components
│       ├── context/     Auth state management
│       ├── services/    API call definitions
│       └── api/         Axios configuration
├── Dockerfile
└── pom.xml
\\\

## API Highlights

- \POST /api/auth/register\ / \POST /api/auth/login\ — Authentication
- \GET /api/policies/search\ — Paginated, filterable policy search
- \PATCH /api/claims/{id}/review\ — Claim approval workflow
- \GET /api/reports/summary\ — Aggregated dashboard statistics
- \POST /api/documents\ — Multipart file upload

Full endpoint list available via the controller classes in \src/main/java/.../controller/\.

## Local Setup

### Backend
\\\ash
git clone https://github.com/AnuvabPol7/insurance-management-platform.git
cd insurance-management-platform
# Set environment variables: IMP_DB_PASSWORD, JWT_SECRET
mvn spring-boot:run
\\\

### Frontend
\\\ash
cd frontend
npm install
npm run dev
\\\

## Key Engineering Decisions

- **Session pooler over transaction pooler** for the database connection — transaction poolers caused connection instability with Hibernate's long-lived connection expectations
- **Centralized exception handling** — every failure mode (validation, auth, malformed input, FK constraints) returns a clean, structured error instead of raw stack traces
- **Role-based UI rendering** — the same pages adapt their content and available actions based on the logged-in user's role, backed by matching server-side authorization

## Author

Anuvab Pal — [GitHub](https://github.com/AnuvabPol7)