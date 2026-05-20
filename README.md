# Bank Management System — Java Spring Boot + MySQL

A production-ready REST API for a full bank management system.

## Tech Stack
- **Java 17**
- **Spring Boot 3.2** (Web, Security, Data JPA, Validation)
- **MySQL 8**
- **JWT Authentication** (jjwt 0.11.5)
- **Lombok**
- **Maven**

---

## Project Structure

```
bank-system/
├── src/main/java/com/bank/
│   ├── BankManagementApplication.java    # Entry point
│   ├── controller/
│   │   ├── AuthController.java           # /api/auth endpoints
│   │   └── BankController.java           # /api/accounts & /api/transactions
│   ├── dto/
│   │   └── Dtos.java                     # All request/response DTOs
│   ├── entity/
│   │   ├── User.java                     # User entity
│   │   ├── Account.java                  # Account entity
│   │   └── Transaction.java              # Transaction entity
│   ├── exception/
│   │   └── GlobalExceptionHandler.java   # Error handling
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── AccountRepository.java
│   │   └── TransactionRepository.java
│   ├── security/
│   │   ├── JwtUtils.java                 # JWT generation + validation
│   │   └── SecurityConfig.java           # Auth filter + Spring Security
│   └── service/
│       └── AccountService.java           # Core business logic
├── src/main/resources/
│   ├── application.properties            # Config (DB, JWT)
│   └── schema.sql                        # MySQL schema + seed
└── pom.xml
```

---

## Setup

### 1. Create MySQL database

```sql
CREATE DATABASE bankdb;
```

Then run `src/main/resources/schema.sql` to create tables and seed an admin user.

### 2. Configure `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bankdb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 3. Build & run

```bash
mvn clean install
mvn spring-boot:run
```

Server starts at `http://localhost:8080`

---

## API Reference

All protected endpoints require header:
```
Authorization: Bearer <token>
```

### Auth

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login, get JWT |

**Register body:**
```json
{
  "username": "rishi",
  "password": "pass1234",
  "fullName": "John Doe",
  "email": "rishi@example.com",
  "phone": "+91-9876543210"
}
```

**Login body:**
```json
{ "username": "rishi", "password": "pass1234" }
```

**Response:**
```json
{
  "token": "eyJhbGci...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "rishi",
  "fullName": "rishi kant",
  "role": "USER"
}
```

---

### Dashboard

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard` | Summary: balance, accounts, monthly stats |

---

### Accounts

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/accounts` | List all accounts |
| GET | `/api/accounts/{id}` | Get account details |
| POST | `/api/accounts` | Open new account |
| DELETE | `/api/accounts/{id}` | Close account (balance must be 0) |

**Create account body:**
```json
{
  "accountName": "My Savings",
  "accountType": "SAVINGS",
  "initialDeposit": 5000.00
}
```
Account types: `SAVINGS`, `CHECKING`, `FIXED_DEPOSIT`

Interest rates (auto-assigned):
- SAVINGS → 3.5%
- FIXED_DEPOSIT → 7.0%
- CHECKING → 0%

---

### Transactions

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions/deposit` | Deposit to account |
| POST | `/api/transactions/withdraw` | Withdraw from account |
| POST | `/api/transactions/transfer` | Transfer between accounts |
| GET | `/api/transactions?accountId=&page=0&size=20` | Paginated history |

**Deposit body:**
```json
{ "accountId": 1, "amount": 1000.00, "description": "Salary" }
```

**Withdraw body:**
```json
{ "accountId": 1, "amount": 200.00, "description": "Rent" }
```

**Transfer body:**
```json
{
  "fromAccountId": 1,
  "toAccountNumber": "ACC1234567",
  "amount": 500.00,
  "description": "Loan repayment"
}
```

---

## Error Responses

All errors return:
```json
{
  "message": "Insufficient funds. Available: 300.00",
  "status": 400,
  "timestamp": "2024-03-22T10:30:00"
}
```

Common HTTP codes:
- `400` — Validation error or insufficient funds
- `401` — Unauthorized / bad credentials
- `403` — Access to another user's account
- `404` — Resource not found
- `409` — Duplicate username or email

---

## Database Tables

| Table | Description |
|-------|-------------|
| `users` | User accounts with roles (USER/ADMIN) |
| `accounts` | Bank accounts linked to users |
| `transactions` | All deposits, withdrawals, transfers |

---

## Testing with curl

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"pass123","fullName":"Alice Smith","email":"alice@example.com"}'

# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"pass123"}' | jq -r .token)

# Create account
curl -X POST http://localhost:8080/api/accounts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"accountName":"My Savings","accountType":"SAVINGS","initialDeposit":5000}'

# View dashboard
curl http://localhost:8080/api/dashboard \
  -H "Authorization: Bearer $TOKEN"
```
