# NTD Arithmetic Calculator

Full-stack implementation of the NTD Software coding challenge. The project
contains a React client and a Spring Boot REST API with JWT authentication,
transactional balance debits, operation history, soft deletion, and Random.org
integration.

## Stack

- Java 21
- Spring Boot 4.1.1
- Spring Security + JWT
- Spring Data JPA
- H2 for lightweight local development/tests
- MySQL 8.4 in Docker Compose
- React 19.3.1
- React Bootstrap 2.10.10
- Gradle 8.14.3
- npm

## Requirements

- JDK 21
- Node.js 22 or later
- npm
- Docker Desktop, only when using Compose

## Run locally without Docker

### 1. Start the API

```powershell
cd server
.\gradlew.bat bootRun
```

The API runs at `http://localhost:8090` and uses an H2 file database under
`server/data` by default. The database is created/updated automatically.

### 2. Start the client

In a second terminal:

```powershell
cd client
npm install
npm start
```

The client runs at `http://localhost:3001`. The development port is 3001
because another local service may use port 3000. API calls are proxied to port
8090.

### 3. Test accounts

The development seed creates these disposable accounts:

| Username | Password | Initial balance |
| --- | --- | ---: |
| `admin@ntd.com` | `123456` | `10.00` |
| `user1@ntd.com` | `123456` | `10.00` |
| `user2@ntd.com` | `123456` | `10.00` |

Do not reuse these credentials in a public deployment.

## Run with Docker Compose

From the repository root:

```powershell
docker compose up --build
```

The services are available at:

- Client: `http://localhost:3001`
- API: `http://localhost:8090`
- MySQL: local Compose network only

Set a deployment-grade JWT secret before starting Compose:

```powershell
$env:JWT_SECRET = "replace-with-a-long-random-secret"
docker compose up --build
```

Stop services and remove the local database volume with:

```powershell
docker compose down -v
```

## API summary

All endpoints except login require:

```text
Authorization: Bearer <jwt>
```

| Method | Endpoint | Purpose |
| --- | --- | --- |
| POST | `/api/v1/users/login` | Authenticate and receive JWT |
| GET | `/api/v1/users/me` | Read current user and balance |
| GET | `/api/v1/calculator` | Execute arithmetic operation |
| GET | `/api/v1/generate-string` | Generate a Random.org string |
| GET | `/api/v1/records` | Search, sort, and paginate own records |
| DELETE | `/api/v1/records?id=<uuid>` | Soft-delete own record |

Full request/response details are in [docs/API.md](docs/API.md).

## Configuration

The API reads environment variables described in

- `SERVER_PORT`
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER`
- `JPA_DDL_AUTO`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS`
- `H2_CONSOLE_ENABLED`

The JWT secret must be changed for any non-local environment. Secrets must not
be committed to the repository.

## Tests and builds

Backend:

```powershell
cd server
.\gradlew.bat test
```

Frontend:

```powershell
cd client
npm test -- --watchAll=false
npm run build
```

## Architecture and decisions

- [Requirements](docs/REQUIREMENTS.md)
- [Architecture](docs/ARCHITECTURE.md)
- [API contract](docs/API.md)
- [Implementation plan](docs/IMPLEMENTATION_PLAN.md)

The balance debit and record creation are performed in one transaction. User
rows are pessimistically locked while the operation is executed, preventing
concurrent requests from spending the same balance.
