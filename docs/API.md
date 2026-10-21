# API contract (v1)

## Login

`POST /api/v1/users/login`

```json
{ "username": "user1@ntd.com", "password": "123456" }
```

Success returns:

```json
{ "token": "<jwt>" }
```

Use the token on protected requests:

```text
Authorization: Bearer <jwt>
```

## Calculator

`GET /api/v1/calculator?operator=%2B&leftOperand=2&rightOperand=3`

The calculator endpoint is authenticated. Supported operators are `+`, `-`,
`*`, `/`, and `sqrt`. The response includes the result, operation cost, and
the user's balance after the debit.

Example response:

```json
{
  "leftOperand": 2.0,
  "rightOperand": 3.0,
  "operation": "+",
  "result": 5.0,
  "message": "Operation completed successfully",
  "userBalance": 9.9,
  "cost": 0.1
}
```

Invalid operands, division by zero, negative square roots, unsupported
operations, and insufficient balance return a structured error response and do
not create a record or debit the balance.

## Random string

`GET /api/v1/generate-string`

The endpoint is authenticated and delegates string generation to Random.org.

## Records

`GET /api/v1/records?page=0&size=10&sort=createdAt,desc`

Records belong to the authenticated user. Search, sorting, pagination metadata,
and soft deletion are part of the completion plan.

Supported query parameters:

- `search`: partial match against operation type or operation response.
- `operationType`: exact operation type filter.
- `page`: zero-based page number.
- `size`: page size, capped at 50.
- `sort`: `createdAt`, `cost`, `userBalance`, or `operationType`, followed by
  `asc` or `desc`.

`DELETE /api/v1/records?id=<uuid>` performs a soft delete. The record remains
in the database but is omitted from normal queries.

## Current user

`GET /api/v1/users/me`

Returns the authenticated user and current balance.

## Error format

```json
{
  "timestamp": "2026-01-01T12:00:00Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Insufficient balance",
  "path": "/api/v1/calculator"
}
```
