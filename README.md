# NTD Challenge — Arithmetic Calculator

This repository contains the React client and Spring Boot API for the NTD
arithmetic calculator challenge.

## Current status

The first implementation milestone is authentication. The development login
credentials are `admin@ntd.com` / `123456` and `user1@ntd.com` / `123456`.

## Run locally

### API

```powershell
cd server
./gradlew.bat bootRun
```

The API runs on `http://localhost:8090`.

### Client

```powershell
cd client
npm install
npm start
```

The client runs on `http://localhost:3001` and proxies API calls to the local
server. Port `3001` is intentional because port `3000` may be occupied by
another local service in the development environment.

## Documentation

- [Requirements](docs/REQUIREMENTS.md)
- [Architecture](docs/ARCHITECTURE.md)
- [API contract](docs/API.md)
- [Implementation plan](docs/IMPLEMENTATION_PLAN.md)
