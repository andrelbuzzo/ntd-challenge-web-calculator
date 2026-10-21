# NTD Calculator — Requirements

## Goal

Deliver a web calculator with authenticated users, per-operation costs, balance
deduction, and a searchable operation history.

## Business rules

- Supported operations: addition, subtraction, multiplication, division, square
  root, and random string generation through Random.org.
- Every operation has a configured cost.
- A request is accepted only when the authenticated user's balance covers its
  cost.
- Balance deduction and record creation must be atomic.
- Division by zero and square root of a negative number are rejected with a
  meaningful error.
- Numbers may be negative.
- Records are soft-deleted and excluded from normal listings.

## Acceptance criteria

- Login returns a token and the UI persists the authenticated session.
- The current balance is visible on authenticated screens.
- Calculator operations run on the backend.
- Records support filtering, pagination, sorting, and soft deletion.
- Passwords are stored as BCrypt hashes.
- Backend and frontend have automated tests.
- Local setup and API usage are documented in the README.

## Demo accounts

The development seed currently creates `admin@ntd.com` and
`user1@ntd.com` through `user10@ntd.com`, all with password `123456`.
These credentials are for local development only and must be replaced or
documented as disposable credentials for a deployed environment.
