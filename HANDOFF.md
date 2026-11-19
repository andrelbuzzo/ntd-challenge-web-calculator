# Handoff — NTD Web Calculator

Date: October 23, 2026

## Goal

Finish the last Records/history-table correction, then separate the React
frontend and Spring Boot backend into independent repositories.

The requested table order is:

```text
date | operation | left operand | operator | right operand | response |
balance before | cost | balance after | actions
```

The actions column must contain an accessible delete button with a trash icon.

## Current state

- Repository: `ntd-challenge-web-calculator`
- Current branch: `main`
- Current revision: `1473d46` — `feat: harden sessions and polish calculator (#6)`
- `main` matches `origin/main`.
- The previous `feature/session-hardening` PR was merged and its remote branch is
  gone; the local branch still exists as a stale local ref.
- `feature/fase-4-hardening` still exists locally and remotely. It is an older
  independent line, 6 commits behind and 2 commits ahead of `origin/main`; its
  unique commits are not the source branch for the merged PR.
- No new commit was created for the Records-table correction yet.
- `client/design.md/` is untracked reference material and must remain outside
  Git.

## Implemented but uncommitted

### Records UI

- Reordered the table columns to the requested format.
- Added readable operation labels and operator symbols (`+`, `−`, `×`, `÷`,
  `√`, `N`).
- Added an icon-only Font Awesome trash button with `aria-label`, `title`, and
  an accessible click target.
- Added responsive horizontal scrolling and minimum column widths.
- Added legacy fallbacks so existing records can recover operands, response,
  operator, and balance-before values from the old stored equation format.

### Records API/domain data

- Added persisted fields to records:
  - `leftOperand`
  - `operator`
  - `rightOperand`
  - `balanceBefore`
- New calculator records persist the actual numeric response rather than the
  old full `Equation [...]` string.
- New records persist the balance before debit and both operands.
- Kept legacy `amount` in the entity/response as a left-operand alias for
  compatibility with existing data.
- Updated `docs/API.md` with the expanded record response fields.

### Tests and state

- Updated Records frontend tests for the new columns, icon delete action, and
  pagination interaction.
- Updated calculator service tests for the expanded record-save arguments.
- Updated `.planning/STATE.md` with this iteration's work.

## Changed files

Intentional changes for this handoff:

- `.planning/STATE.md`
- `client/src/pages/Records/Records.js`
- `client/src/pages/Records/Records.css`
- `client/src/pages/Records/Records.test.js`
- `docs/API.md`
- `server/src/main/java/com/ntd/webcalculator/calculator/CalculatorService.java`
- `server/src/main/java/com/ntd/webcalculator/record/Record.java`
- `server/src/main/java/com/ntd/webcalculator/record/RecordResponse.java`
- `server/src/main/java/com/ntd/webcalculator/record/RecordService.java`
- `server/src/test/java/com/ntd/webcalculator/CalculatorServiceTest.java`

Unrelated existing worktree change to preserve and review separately:

- `client/src/components/Calculator/Calculator.css` changes `.expression`
  `font-size` from `1rem` to `1.5rem`; this was already present as a visual
  adjustment and was not part of the Records-table implementation.

Untracked and intentionally excluded:

- `client/design.md/`

## Decisions

- Do not put the current Records correction directly on `main`; create a new
  fix branch from the current `main` before committing.
- Do not stage `client/design.md/`.
- Preserve the unrelated calculator typography change unless the owner decides
  otherwise.
- Keep the legacy `amount` field in the API response until repository/database
  separation is complete.
- Existing old records may not have the new persisted fields; the frontend
  fallback keeps them readable without requiring destructive data migration.

## Checks

All checks below were run on October 23, 2026 after the Records correction.

### Frontend focused test — passed

Working directory: `client/`

```text
CI=true npm test -- --watchAll=false --runInBand src/pages/Records/Records.test.js
5 tests passed
```

### Frontend full test suite — passed

Working directory: `client/`

```text
CI=true npm test -- --watchAll=false --runInBand
6 suites passed, 22 tests passed
```

### Frontend production build — passed

Working directory: `client/`

```text
npm run build
Compiled successfully.
```

### Backend test suite — passed

Working directory: `server/`

```text
JAVA_HOME=/opt/jdk-21.0.13+9 PATH=/opt/jdk-21.0.13+9/bin:$PATH \
  ./gradlew test --no-daemon --console=plain
BUILD SUCCESSFUL
```

### Diff check — passed

```text
git diff --check
```

Known non-blocking warnings: deprecated CRA/Babel tooling, outdated
Browserslist data, and React Router future-flag warnings in tests.

## Next action

Start the next session with:

```bash
git switch -c fix/records-table-layout
```

Then review the diff, stage only the intentional Records/API/test/state files,
leave `client/design.md/` out, decide whether to include the unrelated
`Calculator.css` typography change, commit the fix, and create the final PR
against `main` if the separation workflow still requires a review boundary.

After the correction is merged and `main` is synchronized, remove the stale
feature branches and proceed with the frontend/backend repository split.
