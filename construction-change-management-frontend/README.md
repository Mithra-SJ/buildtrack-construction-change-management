# BuildTrack – Construction Change Management Frontend

A React + Bootstrap frontend for the Spring Boot Construction Change Management API.

## Requirements
- Node.js 20 or later
- npm
- The backend running at `http://localhost:8080`
- PostgreSQL connected to the backend

## Run locally
1. Extract this folder.
2. Open the folder in VS Code.
3. Open the terminal in this folder.
4. Install packages:

   ```bash
   npm install
   ```

5. Start the frontend:

   ```bash
   npm run dev
   ```

6. Open `http://localhost:5173`.

The Vite development server proxies `/api` requests to `http://localhost:8080`, so the browser can call the backend without adding a separate CORS configuration.

## Features
- Overview dashboard with project and change-request metrics
- Project creation and project listing
- Initial baseline creation
- Change-request creation
- Change-request search and project filter
- Approve/reject actions with an optional decision comment
- Responsive layout for desktop and smaller screens

## API endpoints used
- `GET/POST /api/projects`
- `POST /api/projects/{projectId}/baselines`
- `GET/POST /api/change-requests`
- `POST /api/change-requests/{id}/approve`
- `POST /api/change-requests/{id}/reject`

## Important notes
- This UI matches the current MVP backend contract. It does not add authentication or role-based permissions because those endpoints are not currently implemented in the backend.
- Approval and rejection controls are shown for DRAFT, SUBMITTED, or PENDING requests to match the current backend's simple decision flow.
- The dashboard cost impact is calculated from approved change-request estimates; it is not a full revised project forecast.
- For production deployment, add authentication, role-based access, audit history, API pagination, and environment-specific API configuration.
