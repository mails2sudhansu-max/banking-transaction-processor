# Banking Transaction Processor

Coding-kata implementation using **Spring Boot 4.3.4, Java 17 and H2 in-memory DB**.

## Requirements covered
- Unique account IDs and balances
- Deposit, withdrawal and transfer
- Invalid amount and overdraft validation
- Per-account transaction ledger with timestamps
- Balance and transaction-history APIs
- Transactional transfers with rollback
- Pessimistic locking for concurrent transfers
- Comprehensive unit and MockMvc integration tests

## Why H2?
The requirement asks for an in-memory database. H2 provides a real relational database while remaining completely self-contained. `jdbc:h2:mem:bankdb` means data disappears when the application stops.

## API
- `POST /api/accounts`
- `GET /api/accounts/{id}/balance`
- `POST /api/accounts/{id}/deposits`
- `POST /api/accounts/{id}/withdrawals`
- `POST /api/transfers`
- `GET /api/accounts/{id}/transactions?from=...&to=...`

## Example
```bash
mvn clean test
mvn spring-boot:run
```

Create account:
```bash
curl -X POST http://localhost:8980/api/accounts -H 'Content-Type: application/json' -d '{"accountId":"A1","initialBalance":1000.00}'
```

Transfer:
```bash
curl -X POST http://localhost:8980/api/transfers -H 'Content-Type: application/json' -d '{"fromAccountId":"A1","toAccountId":"A2","amount":100.00,"description":"payment"}'
```

## Test strategy
Tests cover account creation, duplicate IDs, initial balance validation, deposits, withdrawals, overdrafts, zero/negative amounts, decimal precision, missing accounts, self-transfer, transfer ledger entries, transfer rollback, API validation, HTTP status/error mapping and transaction history.

## Important design decision
A transfer is one database transaction. Both account rows are locked before balances are changed. If the debit fails, Spring rolls the entire operation back, so the destination cannot receive money when the source cannot pay.

## Submission notes
Before submitting, run `mvn clean test`, inspect the generated code, and create incremental Git commits such as `initial domain`, `account operations`, `transfer transaction`, `REST API`, `tests`, and `documentation` so the requested iteration is visible.
