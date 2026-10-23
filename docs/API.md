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
and soft deletion are part of the contract. Normal record queries exclude
soft-deleted records.

Supported query parameters:

- `search`: partial match against operation type or operation response.
- `operationType`: one or more operation type filters. Values may be supplied as
  a comma-separated list (`operationType=ADDITION,DIVISION`), repeated query
  parameters (`operationType=ADDITION&operationType=DIVISION`), or both. Values
  are case-insensitive, surrounding whitespace is ignored, and duplicate values
  are collapsed. Supported values are `ADDITION`, `SUBTRACTION`,
  `MULTIPLICATION`, `DIVISION`, `SQUARE_ROOT`, and `RANDOM_STRING`. Unsupported
  values return `400 Bad Request`. Stored operation type values remain the
  existing uppercase names.
- `page`: zero-based page number.
- `size`: page size, capped at 50.
- `sort`: `createdAt`, `cost`, `userBalance`, or `operationType`, followed by
  `asc` or `desc`.

`DELETE /api/v1/records?id=<uuid>` performs a soft delete. The record remains
in the database but is omitted from normal queries. A record can only be
deleted by its owner; an unknown or another user's record returns `404`.

`PATCH /api/v1/records/undelete?id=<uuid>` restores an owned soft-deleted
record. It is also ownership-scoped and returns `404` when the record is not
owned by the authenticated user.

Successful list responses contain `content` plus zero-based `page`, effective
`size`, `totalElements`, `totalPages`, and `first`/`last` pagination metadata.
Each record in `content` exposes `createdAt`, `operationType`, `leftOperand`,
`operator`, `rightOperand`, `operationResponse`, `balanceBefore`, `cost`, and
`userBalance` (the balance after the operation). Legacy `amount` remains in the
payload as an alias for the left operand for compatibility with existing data.

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
