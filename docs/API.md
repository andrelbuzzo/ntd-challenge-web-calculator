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
