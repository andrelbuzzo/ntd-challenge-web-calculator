# Project State

> Persistent context for resuming work between sessions. Update this file at
> the end of every meaningful iteration.

## Last updated

- Date: October 23, 2026
- Branch: `feature/session-hardening`
- Base commit: `d56c67e` (`feat(app): harden sessions and records contract`)
- Iteration: calculator alert/footer polish after authentication, readability, and navigation pass

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
- Updated the browser title and PWA manifest name to `NTD Software - Web Calculator`.
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
- Made calculator alerts overlay the card instead of changing its layout height.
- Increased UI text sizes, header height, control sizing, and responsive spacing
  across the calculator, home, login, and records surfaces.
- Added keyboard calculator shortcuts for digits, decimal point, operators,
  Enter/Equals, Escape, Backspace, square root (`R`), and random string (`N`).
- Added calculator tests covering keyboard calculation, clear, square root, and
  random-string shortcuts.
- Verified the local H2 database starts successfully and contains the seeded
  development users; direct and client-proxy login both returned `200`.
- Improved login error handling so an unavailable API is no longer reported as
  invalid credentials, and added regression coverage for the network-failure
  message.
- Increased typography, button heights, spacing, and header dimensions again
  across the calculator actions/keypad, home aside, app header, and Records
  page after a readability review.
- Stopped the React development server and Spring Boot/Gradle runtime after
  verification; ports `3001` and `8090` are closed.
- Increased all header label sizes and added accessible Font Awesome icons to
  the brand mark, Calculator link, History link, and Sign out button.
- Moved the live balance into the Home page's `CONSUMPTION TRACKING` block and
  removed the calculator top-line wrapper.
- Forced calculator alerts above Bootstrap's `.fade` opacity rule so errors and
  success messages remain visible without changing calculator layout height.
- Pinned the footer to the viewport bottom with the application flex layout and
  `margin-top: auto`.
- Renamed the visible application branding to `NTD Software - Web Calculator`
  based on the challenge PDF.
- Added automatic four-second dismissal and manual close controls to calculator
  Bootstrap alerts.
- Kept calculator controls square while applying bordered, rounded controls to
  login, navigation, records, and other non-calculator actions.
- Rounded login inputs/buttons and the header sign-out control to 5px, and
  switched Calculator/History to `NavLink` active styling for selected-state
  feedback.
- Corrected the Bootstrap alert close control: it now has an explicit in-layout
  36px hit area, centered `×` icon, focus/hover state, and reserved text space;
  added a regression test for manual dismissal.
- Repositioned calculator alerts as centered fixed overlays aligned to the
  responsive header's vertical midpoint, with a short entrance animation and
  reduced-motion support.
- Replaced the footer's descriptive span with accessible LinkedIn and GitHub
  icon links pointing to André Buzzo's profiles.
- Reworked the Records table to show date, operation, operands, operator,
  response, balances before/after, cost, and an icon-only delete action.
- Added record fields for left/right operands, operator, and balance before;
  new calculator history entries now persist the actual response and full
  operation context while retaining the legacy `amount` response field.
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

Result: 6 test suites passed, 22 tests passed, and the production build
compiled successfully after the alert/footer changes.

The calculator readability, shortcut, login error-handling, alert positioning,
and footer-link changes passed the full frontend verification on October 23,
2026. The development client
served the updated app successfully on port `3001`, and the local API served on
port `8090` with H2 and completed a real login/proxy smoke check.

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
  `NTD Software - Web Calculator` title and React root.

The Records table/API update also passed the full frontend suite, production
build, and backend test suite after adding the operation-context fields.

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

1. Review and commit the current calculator, readability, login error handling,
   alert positioning, and footer-link changes on the current feature branch.
2. Keep the ignored dependency/build artifacts and untracked design reference
   material out of version control unless explicitly requested.
3. Revisit CI once GitHub Actions quota is available, as tracked in
   `docs/TECHNICAL_DEBT.md`.

## Maintenance policy

At the end of every meaningful iteration, update this file with:

- date and branch;
- objective and completed changes;
- tests/builds actually executed and their real results;
- known blockers or risks;
- concrete next steps.
