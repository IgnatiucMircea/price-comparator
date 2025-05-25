# Price Comparator - Market

## Overview

Price Comparator is a backend REST API that compares supermarket product prices, tracks discounts, and helps users optimize their shopping basket across Lidl, Kaufland, and Profi.

---

## Features

* Compare product prices across stores
* Get the best price or discount for a product
* List top/new discounts
* Track price history over time
* Get best value per unit and substitutes
* Optimize basket across stores
* Price alerts for products

---

## Getting Started

### Prerequisites

* Java 17+
* Gradle

### Setup

1. Clone the repository:

```bash
git clone <https://github.com/IgnatiucMircea/price-comparator>
cd price-comparator
```


### Running the App

```bash
./gradlew bootRun
```

Backend will be available at:

```
http://localhost:8080/api
```

---

## API Endpoints

\*All endpoints are relative to \**`/api`*

### General

* **GET /api**

    * List all base products (from `products.csv`).

* **GET /api/products/{store}/{date}**

    * List all products and their prices in a store on a specific date.
    * Example: `/api/products/lidl/2025-05-01`

### Price, Discounts & Basket

* **GET /api/best-price/{productId}?date=YYYY-MM-DD**

    * Best price for a product across stores on a specific date.

* **GET /api/best-discount/{productId}?date=YYYY-MM-DD**

    * Best discount for a product on a specific date.

* **POST /api/basket/best-split?date=YYYY-MM-DD**

    * Optimized basket split.
    * Request body (JSON):

```json
[
  { "productId": "P001", "quantity": 2 },
  { "productId": "P014", "quantity": 1 }
]
```

### Discounts

* **GET /api/discounts/{store}/{date}**

    * All discounts for a store on a specific date.

* **GET /api/discounts/top?date=YYYY-MM-DD**

    * Top discounts for the given date.

* **GET /api/discounts/new?date=YYYY-MM-DD**

    * Discounts newly added on that date.

### Price History

* **GET /api/products/{productId}/price-history**

    * Price history for a product across dates/stores.

### Value & Substitutes

* **GET /api/products/best-value?date=YYYY-MM-DD\[\&category=...]**

    * Best value-per-unit products, optionally filtered by category.

* **GET /api/products/{productId}/substitutes?date=YYYY-MM-DD**

    * Substitute products in same category, sorted by value/unit.

### Price Alert

* **POST /api/products/{productId}/alert?date=YYYY-MM-DD**

    * Check if a product price is at or below a target.
    * Request body:

```json
{ "targetPrice": 7.5 }
```

* Returns: `true` or `false`

---

## Example Usage

```bash
# Get products in Lidl on a date
Invoke-RestMethod -Uri "http://localhost:8080/api/products/lidl/2025-05-01"

# Best price for a product
Invoke-RestMethod -Uri "http://localhost:8080/api/best-price/P001?date=2025-05-01"

# Basket optimization (best split across stores)
$body = '[{"productId":"P001","quantity":2}]'
Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8080/api/basket/best-split?date=2025-05-01" `
  -Body $body `
  -ContentType "application/json"

# Price alert (check if product reached a target price)
$alertBody = '{"targetPrice": 7.50}'
Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8080/api/products/P001/alert?date=2025-05-01" `
  -Body $alertBody `
  -ContentType "application/json"
```

---

## Testing

Run unit tests:

```bash
./gradlew test
```

---

## Project Structure

* `src/main/java` — Java source
* `src/main/resources/prices` — price CSVs
* `src/main/resources/discounts` — discount CSVs
* `src/test/java` — unit tests

---

## Author

Ignatiuc Mircea-Andrei

