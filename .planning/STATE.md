# Project State

> Persistent context for resuming work between sessions. Update this file at
> the end of every meaningful iteration.

## Last updated

- Date: October 23, 2026
- Branch: `feature/session-hardening`
- Base commit: `a21a197` (`feat(frontend): modernize calculator and records`)
- Iteration: session hardening, Records contract, repository hygiene, and runtime verification

## Current objective

Keep the NTD challenge full-stack application functional, documented, and ready
for local validation while preserving the contracts between the React client
and Spring Boot API.

The product UI, user-facing messages, tests, and project state are written in
English to match the American-company challenge context.

## Completed in this iteration

- Created this `STATE.md` because no previous project state file existed.
- Translated the remaining Portuguese UI copy to English across navigation,
  login notices, calculator, home, and records screens.
- Updated accessibility labels, confirmation messages, empty/loading states,
  table headings, sorting labels, operation filters, and pagination controls.
- Updated frontend fixtures and assertions to match the English UI/API messages.
- Updated the browser title and PWA manifest name to `Ledger / Calculate`.
- Preserved API paths, DTO fields, persisted enum values, and database-facing
  identifiers such as `ADDITION` and `SQUARE_ROOT`.
- Added consistent `/home` handling for expired sessions, including storage
  cleanup, `auth-expired` dispatch, redirect, and focused frontend coverage.
- Hardened Records operation-type filtering to accept repeated and
  comma-separated values, normalize case/whitespace, remove duplicates, and
  reject unsupported values with `400 Bad Request`.
- Documented Records filtering, pagination metadata, ownership-scoped soft
  deletion, and the API-only undelete endpoint.
- Added Records service/controller coverage and API-client coverage for the
  updated contract.
- Corrected the client undelete request to send the record id as a query
  parameter in Axios config rather than as a request body.
- Restored tracked files that contained only line-ending changes; preserved the
  meaningful application diff and the untracked design reference directory.

## Verification status

### Passed

The English-copy changes passed the full frontend verification:

```text
cd client
CI=true npm test -- --watchAll=false --runInBand
npm run build
```

Result: 5 test suites passed, 13 tests passed, and the production build
compiled successfully.

The resumed iteration passed the expanded frontend verification:

```text
cd client
CI=true npm test -- --watchAll=false --runInBand
npm run build
```

Result: 6 test suites passed, 17 tests passed, and the production build
compiled successfully.

Expected non-blocking warnings include the deprecated Create React App toolchain,
an outdated Browserslist database, and React Router future-flag warnings in
tests.

### Backend environment

The sandbox initially lacked both `react-scripts` and Java. Dependencies were
restored with `npm ci`, and a temporary JDK 21 runtime was installed outside
the repository for local verification.

`./gradlew clean test --no-daemon --console=plain` was attempted, but Gradle
could not delete the mounted `server/build` directory. The equivalent test
task without `clean` completed successfully after recompilation.

Backend verification command and result:

```text
cd server
JAVA_HOME=/opt/jdk-21.0.13+9 PATH=/opt/jdk-21.0.13+9/bin:$PATH \
  ./gradlew test --no-daemon --console=plain
BUILD SUCCESSFUL
```

The full backend suite passed after the Records contract changes. The focused
Records tests also passed before the full suite.

Runtime smoke checks passed locally:

- Login returned `200` and a JWT for the seeded development account.
- Unauthenticated `/api/v1/users/me` returned `403`.
- Authenticated `/api/v1/users/me` and `/api/v1/records` returned `200` with
  the expected user and pagination payloads.
- The production client served on port `3001` and contained the expected
  `Ledger / Calculate` title and React root.

On Windows, use the committed `gradlew.bat` wrapper.

## Worktree notes

- Many pre-existing files show line-ending-only differences; they have not
  been reverted.
- `client/design.md/` is untracked and contains design/skill reference
  material; it was not changed in this iteration.
- The intentional functional changes in this iteration are the English copy,
  related frontend test updates, session handling, Records contract changes,
  API documentation, and project state updates.

## Next steps

1. Keep the ignored dependency/build artifacts and untracked design reference
   material out of version control unless explicitly requested.
2. Revisit CI once GitHub Actions quota is available, as tracked in
   `docs/TECHNICAL_DEBT.md`.

## Maintenance policy

At the end of every meaningful iteration, update this file with:

- date and branch;
- objective and completed changes;
- tests/builds actually executed and their real results;
- known blockers or risks;
- concrete next steps.
