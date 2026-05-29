# RAMBO — Inventory & Sales System

A real inventory system built for a mechanic shop, used to track products, sales, stock alerts, and profit margins. Replaces WhatsApp-based tracking with a proper REST API and Android app.

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language / Runtime |
| Spring Boot | 3.3 | Web framework |
| PostgreSQL | 16 | Primary database |
| Spring Data JPA | 3.3 | ORM / data access |
| ModelMapper | 3.2 | Entity ↔ DTO mapping |
| Lombok | latest | Boilerplate reduction |
| Docker / Docker Compose | latest | Containerization |
| Capacitor | latest | Android APK packaging |

---

## Architecture

```
HTML Frontend (Capacitor / Android)
         │
         ▼  HTTP / JSON
┌──────────────────────────────┐
│       REST Controllers       │  ← validate input, delegate to service
│  /api/products  /api/sales   │
│  /api/dashboard              │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│         Services             │  ← all business logic lives here
│  ProductService  SaleService │
│  DashboardService            │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│        Repositories          │  ← data access only (Spring Data JPA)
│  ProductRepository           │
│  SaleRepository              │
└───────────┬──────────────────┘
            │
            ▼
┌──────────────────────────────┐
│        PostgreSQL 16         │
│  products  │  sales          │
└──────────────────────────────┘
```

---

## Features

- **Product inventory** — create, update, restock, and delete products
- **Sales registration** — record sales with automatic stock deduction
- **Multi-currency support** — register products and sales in PEN (soles) or USD (dollars); the system converts to PEN at the recorded exchange rate and stores the original amount for a full audit trail
- **Live exchange rate** — fetches the current USD/PEN rate automatically on page load from a free public API; user can override the rate per transaction
- **Profit margin calculation** — per product and aggregated across all sales
- **Low stock alerts** — flag products at or below their minimum threshold
- **Business dashboard** — KPIs: revenue, net profit, average margin, top products
- **Monthly close report** — filter sales history by month/year with period totals
- **Responsive UI** — works on desktop and mobile; bottom navigation on small screens
- **Android APK** — packaged via Capacitor for use on a phone at the shop

---

## Multi-Currency

Sales and products can be entered in either **PEN (soles)** or **USD (dollars)**.

- The exchange rate is fetched automatically from `currency-api.pages.dev` on page load and can be overridden per session.
- All amounts are stored internally in PEN for consistent reporting.
- For USD sales, the original dollar amount, the rate used, and the PEN equivalent are all stored.

**Sale request with USD:**
```json
{
  "productId": 4,
  "quantity": 1,
  "currency": "USD",
  "unitPrice": 50.00,
  "exchangeRate": 3.40
}
```

**Sale response:**
```json
{
  "unitPrice": 170.00,
  "currency": "USD",
  "exchangeRateUsed": 3.40,
  "originalUnitPrice": 50.00
}
```

---

## API Endpoints

### Products — `/api/products`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/products` | List all products (alphabetical, paginated) |
| `GET` | `/api/products/{id}` | Get a single product |
| `GET` | `/api/products/low-stock` | Products at or below minimum stock |
| `GET` | `/api/products/category/{category}` | Filter products by category |
| `POST` | `/api/products` | Create a new product |
| `PUT` | `/api/products/{id}` | Full update of a product |
| `PATCH` | `/api/products/{id}/restock?quantity=N` | Add stock without changing other fields |
| `DELETE` | `/api/products/{id}` | Delete a product (cascades to its sales) |

### Sales — `/api/sales`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/sales` | List all sales (paginated) |
| `GET` | `/api/sales/{id}` | Get a single sale |
| `GET` | `/api/sales/product/{productId}` | Sales history for a specific product |
| `POST` | `/api/sales` | Register a new sale (deducts stock automatically) |
| `DELETE` | `/api/sales/{id}` | Delete a sale (restores stock automatically) |

### Dashboard — `/api/dashboard`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/dashboard` | Full business summary (KPIs + low stock + top products) |

Interactive API documentation is available at `http://localhost:8080/swagger-ui.html` when the server is running.

---

## Error Responses

All errors follow a consistent shape:

```json
{
  "status": 400,
  "message": "Validation failed for one or more fields",
  "timestamp": "2026-05-29T07:00:00",
  "fields": { "quantity": "Minimum quantity is 1" }
}
```

| HTTP Status | When |
|---|---|
| `400` | Validation failure, missing required fields, invalid argument |
| `404` | Product or sale not found |
| `409` | Duplicate product name |
| `422` | Insufficient stock for requested sale quantity |
| `500` | Unexpected internal error |

---

## Production

| Resource | URL |
|---|---|
| **Frontend + API** | https://rambo-inventario.up.railway.app |
| **Platform** | Railway (Hobby plan) |
| **Database** | PostgreSQL 16 — managed by Railway |

The frontend (`index.html`) is served directly by Spring Boot from `src/main/resources/static/`. Any push to `main` triggers an automatic redeploy on Railway.

---

## Running Locally

### Option A — Docker (recommended)

Requires Docker Desktop installed.

1. Copy the environment file and fill in your password:
   ```bash
   cp .env.example .env
   # edit .env and set DB_PASSWORD
   ```
2. Start everything:
   ```bash
   docker compose up --build
   ```
3. The API is available at `http://localhost:8080`.

### Option B — Manual (mvn + local PostgreSQL)

1. Install PostgreSQL 16 and create the database:
   ```sql
   CREATE DATABASE rambo_db;
   ```
2. Copy and configure the environment:
   ```bash
   cp .env.example .env
   # set DB_USERNAME, DB_PASSWORD to match your local PostgreSQL
   ```
3. Export the variables and run:
   ```bash
   export $(cat .env | xargs)
   mvn spring-boot:run
   ```
4. The API is available at `http://localhost:8080`.

---

## Environment Variables

| Variable | Description | Example |
|---|---|---|
| `DB_USERNAME` | PostgreSQL username | `postgres` |
| `DB_PASSWORD` | PostgreSQL password | `your_password_here` |
| `POSTGRES_DB` | Database name | `rambo_db` |
| `SPRING_DATASOURCE_URL` | Full JDBC URL (overrides host/db defaults) | `jdbc:postgresql://localhost:5432/rambo_db` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Schema mode (`update` or `validate`) | `update` |

Copy `.env.example` to `.env` and fill in the values. The `.env` file is gitignored and must never be committed.

---

## Project Structure

```
src/main/java/com/rambo/
├── RamboApplication.java
├── config/
│   ├── CorsConfig.java
│   └── ModelMapperConfig.java
├── controller/
│   ├── DashboardController.java
│   ├── ProductController.java
│   └── SaleController.java
├── dto/
│   ├── DashboardDTO.java
│   ├── ProductRequestDTO.java
│   ├── ProductResponseDTO.java
│   ├── SaleRequestDTO.java
│   ├── SaleResponseDTO.java
│   └── TopProductDTO.java
├── entity/
│   ├── Currency.java
│   ├── Product.java
│   └── Sale.java
├── exception/
│   ├── ConflictException.java
│   ├── GlobalExceptionHandler.java
│   ├── InsufficientStockException.java
│   └── ResourceNotFoundException.java
├── mapper/
│   ├── ProductMapper.java
│   └── SaleMapper.java
├── repository/
│   ├── ProductRepository.java
│   └── SaleRepository.java
└── service/
    ├── DashboardService.java
    ├── ProductService.java
    ├── SaleService.java
    └── impl/
        ├── DashboardServiceImpl.java
        ├── ProductServiceImpl.java
        └── SaleServiceImpl.java
```

---

## About This Project

Built by Miguel Rolando Salas Guillen for my father's mechanic shop to replace WhatsApp-based inventory tracking with a real system. The shop needed a way to know what parts were in stock, how much they cost, what the profit margin was on each sale, and when to reorder. This backend powers a web frontend and an Android app (via Capacitor) that runs on a phone kept at the shop counter.
