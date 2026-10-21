# Agent instructions

## Repository shape

- This is a two-stack repository, not a root npm/Gradle workspace: run frontend commands from `client/` and backend commands from `server/`.
- `server/` is a Spring Boot 4.1.1 REST API using Java 21, Gradle 8.14.3, Spring Security/JWT, JPA, and H2 by default; `client/` is a React 19 CRA app using React Router 6.
- The backend entry point is `server/src/main/java/com/ntd/webcalculator/WebCalculatorApplication.java`; it seeds disposable development users on startup.
- The frontend entry point is `client/src/App.js`; it owns authentication state and protected routes for `/home` and `/records`.
- Operation execution, balance debit, and record creation are one transactional backend use case; preserve user ownership scoping on every Record query/mutation.

## Commands

- Backend tests: `cd server; .\gradlew.bat clean test --no-daemon --console=plain`.
- A focused backend test uses Gradle's fully qualified filter, for example: `cd server; .\gradlew.bat test --tests com.ntd.webcalculator.SecurityIntegrationTest`.
- Frontend tests: `cd client; $env:CI='true'; npm test -- --watchAll=false --runInBand`.
- A focused frontend test can pass a test path after the CRA flags, for example: `cd client; npm test -- --watchAll=false --runInBand src/pages/Login/Login.test.js`.
- Frontend production build: `cd client; npm run build`.
- Local API: `cd server; .\gradlew.bat bootRun` (port `8090`); local client: `cd client; npm install; npm start` (port `3001`, proxying `/api` to `8090`).
- Compose is run from the repository root; use `docker compose up -d --force-recreate` when replacing stale containers, and `docker compose down -v` when intentionally resetting the MySQL volume.

## Environment and testing

- `server/src/main/resources/application.yaml` loads database, JWT, CORS, and port settings from environment variables; local defaults use an H2 file under `server/data`.
- Compose switches the API to MySQL 8.4 and waits for the database healthcheck before starting the API; set a deployment-grade `JWT_SECRET` instead of the local fallback outside development.
- Backend tests use the default H2 configuration and do not require Docker; use Compose only for MySQL/runtime integration checks.
- Development seed credentials are documented in the root `README.md`; they are disposable and must not be reused in a public deployment.
- `client/.env.development` intentionally sets port `3001`; changing it requires matching the API CORS origin and Compose client port.

## Change boundaries

- Keep API contract changes synchronized between backend controllers/DTOs, `docs/API.md`, and `client/src/utils/API.js`.
- Keep generated/build/local state out of changes: `server/build/`, `server/.gradle/`, `client/build/`, `node_modules/`, H2 files, `.env` files, IDE metadata, and the challenge PDF are ignored.
- When changing security, balance, or Record behavior, run the focused security/Record tests plus the full backend test task; these paths have integration coverage and ownership/concurrency invariants.
- Prefer the Gradle wrapper (`server/gradlew.bat`) and the committed lockfiles over globally installed tools.
