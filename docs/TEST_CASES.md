# Test cases

| Area | Scenario | Expected |
|---|---|---|
| Account | Create unique account | 201 |
| Account | Duplicate ID | 409 |
| Account | Negative initial balance | 400 |
| Deposit | Positive amount | Balance increases + ledger entry |
| Deposit | Zero/negative | 400 |
| Deposit | >2 decimal places | 400 |
| Withdrawal | Sufficient funds | Balance decreases + ledger entry |
| Withdrawal | Exact balance | Balance becomes zero |
| Withdrawal | Overdraft | 409; no ledger entry |
| Transfer | Successful | Both balances updated + 2 ledger entries |
| Transfer | Same account | 400 |
| Transfer | Missing source/target | 404 |
| Transfer | Insufficient source funds | 409; transaction rolled back |
| History | Existing account | Ordered ledger |
| History | Unknown account | 404 |
| History | Invalid from/to range | 400 |
| API | Missing required fields | 400 |
