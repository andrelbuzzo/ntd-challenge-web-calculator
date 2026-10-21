# Implementation plan

## Phase 1 — Authentication (current)

- Update supported framework/runtime versions.
- Make login validation and API error handling deterministic.
- Persist the token only after a successful response.
- Fix route matching and authenticated navigation.
- Add backend and frontend login tests.

## Phase 2 — Transactional calculator

- Move all calculator business logic out of controllers.
- Add transactional balance debit and record creation.
- Add domain exceptions and a global error response.
- Correct operation mapping and validation.

## Phase 3 — Records and balance UI

- Scope records to the authenticated user.
- Return pagination and sorting metadata.
- Implement the Records table, search, sorting, pagination, and soft delete.
- Keep the balance visible and synchronized after every operation.

## Phase 4 — Delivery hardening

- Add meaningful unit and integration tests.
- Add Docker Compose and environment examples.
- Remove generated IDE/build/database artifacts from version control.
- Complete README, API documentation, and deployment instructions.
