# GroceryStore - React + Spring Boot + MySQL

A full-stack grocery shopping application with cross-platform support for Android and Web.

## Project Structure

```
Grocerystore/
├── grocery-backend/      # Spring Boot Backend
├── grocery-frontend/    # React Frontend
└── app/                 # Android App (existing)
```

## Quick Start

### Prerequisites

- Java 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### 1. Setup MySQL Database

```sql
CREATE DATABASE grocery_store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Update `grocery-backend/src/main/resources/application.yml` with your MySQL credentials:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/grocery_store
    username: root
    password: your_password
```

### 2. Start Backend

```bash
cd grocery-backend
mvn spring-boot:run
```

The backend will run on `http://localhost:8080`

### 3. Start Frontend

```bash
cd grocery-frontend
npm install
npm run dev
```

The frontend will run on `http://localhost:5173`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `GET /api/auth/me` - Get current user

### Products
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get product details
- `GET /api/products/featured` - Get featured products
- `GET /api/products/search?q=` - Search products

### Categories
- `GET /api/categories` - List all categories
- `GET /api/categories/{id}` - Get category with products

### Cart (Requires Auth)
- `GET /api/cart` - Get cart
- `POST /api/cart/items` - Add to cart
- `PUT /api/cart/items/{id}` - Update quantity
- `DELETE /api/cart/items/{id}` - Remove from cart

### Orders (Requires Auth)
- `GET /api/orders` - List orders
- `GET /api/orders/{id}` - Get order details
- `POST /api/orders` - Create order

## Features

- Multi-language support (Chinese, English, Russian)
- Responsive design (Mobile & Desktop)
- JWT Authentication
- Shopping cart with discount logic
- Order management
- Price calculation: 10% off over ¥100, additional ¥20 off over ¥200
