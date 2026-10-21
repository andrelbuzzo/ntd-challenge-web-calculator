# Technical debt

## TD-001 — GitHub Actions CI

- **Status:** Deferred until the GitHub Actions quota is available again.
- **Reason:** The repository's current Actions quota is exhausted during development.
- **Impact:** Pull requests do not receive automatic backend/frontend test or build checks; validation must remain local.
- **Completion criteria:** Add a GitHub Actions workflow that runs the backend test suite, frontend test suite, and frontend production build on pull requests and pushes to `main`.
- **Revisit:** Before the next production release or when CI quota is restored, whichever comes first.
