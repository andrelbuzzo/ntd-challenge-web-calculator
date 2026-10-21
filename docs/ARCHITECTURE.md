# Architecture

The repository contains two independently runnable stacks:

- `server/`: Spring Boot 4 REST API, JWT authentication, JPA persistence, and
  service-layer business logic.
- `client/`: React single-page application consuming only the versioned REST
  API.

## Current direction

The backend is being organized as a modular service application with separate
user, calculator, operation, and record modules. The operation use case will be
the transaction boundary: lock the user, validate balance, calculate, deduct,
and persist the record in one transaction.

## Security

- Login is public at `POST /api/v1/users/login`.
- Authenticated requests use `Authorization: Bearer <token>`.
- Passwords use BCrypt.
- JWT secrets must come from environment configuration before deployment.

## Planned persistence guarantees

User balance updates must use a transaction and pessimistic row locking (or an
equivalent atomic update). Record queries must always be scoped to the current
authenticated user unless an explicitly authorized administrative endpoint is
introduced.
