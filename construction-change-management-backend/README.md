# Construction Change Management System — Backend

Java 25 (LTS) / Spring Boot 3.5.16 / Maven / PostgreSQL.

## Implemented endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Health check |
| POST | `/api/projects` | Create project |
| GET | `/api/projects` | List projects |
| GET | `/api/projects/{id}` | Get project |
| POST | `/api/projects/{id}/baselines` | Create next baseline snapshot |
| GET | `/api/projects/{id}/baselines` | List baseline versions |
| POST | `/api/change-requests` | Create a change request |
| GET | `/api/change-requests?projectId=1` | List change requests |
| POST | `/api/change-requests/{id}/approve` | Approve draft/submitted request |
| POST | `/api/change-requests/{id}/reject` | Reject draft/submitted request |

## Run locally

1. Create a PostgreSQL database named `construction_db`.
2. Check `src/main/resources/application.properties` and update username/password if needed.
3. Install JDK 25 and Maven 3.9+.
4. From this directory run:

```powershell
mvn clean test
mvn spring-boot:run
```

Health check: `http://localhost:8080/api/health`

## Example: create a project

POST `http://localhost:8080/api/projects`

```json
{
  "projectCode": "BLD-001",
  "name": "Demo Commercial Building",
  "budget": 50000000,
  "startDate": "2026-01-01",
  "plannedEndDate": "2027-06-30"
}
```

## Example: create a change request

POST `http://localhost:8080/api/change-requests`

```json
{
  "projectId": 1,
  "title": "Add two floors",
  "description": "Client requested two additional floors.",
  "category": "SCOPE",
  "estimatedCost": 6000000,
  "estimatedDelayDays": 60
}
```

## Important scope notes

This is a functional learning MVP for project CRUD, baseline snapshots, change request creation and one-step decisions. It is not production-ready: authentication/authorization, multi-stage approval, immutable audit history, estimate versioning, full CPM scheduling, forecast reconciliation, pagination and comprehensive automated tests are not included. Approval currently accepts DRAFT or SUBMITTED requests directly; add an explicit submit endpoint and stricter workflow rules before relying on it. `ddl-auto=update` is for local development; use migrations such as Flyway for controlled deployments.

## Minore changes for Commit

Change 1