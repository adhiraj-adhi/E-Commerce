# Ecommerce-APP Backend

A full-featured **E-commerce backend application** built with **Spring Boot**, providing RESTful APIs for product, category, cart management and user authentication using JWT-based security.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Complete Model Diagram](#complete-model-diagram)
- [Security](#security)
- [Exception Handling](#exception-handling)
- [File Upload](#file-upload)
- [Contributing](#contributing)

---

## Features

- Manage product categories and products with pagination, sorting, and searching capabilities.
- Add, update, and delete categories and products.
- Manage shopping carts with add, update quantity, and remove product functionalities.
- Handle user roles: Admin, Seller, and User with role-based access control.
- Secure APIs with JWT (JSON Web Tokens) authentication and authorization.
- Upload and update product images.
- Global exception handling with meaningful API responses.

---

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.5.5
- **Security:** Spring Security, JWT (io.jsonwebtoken)
- **Persistence:** Spring Data JPA, Hibernate
- **Database:** MySQL (or H2 for in-memory testing)
- **Build Tool:** Maven
- **Utilities:** ModelMapper, Lombok, Validation API

---

## API Endpoints

See your controllers for available endpoints, including:

- Public Endpoints:
- `GET /api/public/categories` - List categories
- `GET /api/public/products` - List products
- `GET /api/public/categories/{categoryId}/products` - Products by category
- `GET /api/public/products/keyword/{keyword}` - Search products by keyword

- Admin Endpoints:
- `POST/PUT/DELETE /api/categories` - Manage categories
- `GET /api/carts` - View all carts
- `GET /api/products/count` - Product count

- Seller Endpoints:
- `POST /api/categories/{categoryId}/product` - Add product
- `PUT /api/products/{productId}` - Update product
- `DELETE /api/products/{productId}` - Delete product
- `PUT /api/products/{productId}/image` - Update product image

- User Endpoints:
- `POST /api/carts/products/{productId}/quantity/{quantity}` - Add to cart
- `PATCH /api/carts/products/{productId}/quantity/{operation}` - Update cart item quantity
- `DELETE /api/carts/product/{productId}` - Remove product from cart
- `GET /api/carts/users/cart` - Get user's cart items

---

## Project Structure
src/main/java/com/project/ecom/  
├── config/  
├── controllers/  
├── dao/ # Repositories  
├── exceptions/  
├── model/  
├── payload/ # DTOs and Response classes  
├── security/ # JWT and Spring Security  
├── services/ # Business logic  
└── util/

---

## Complete Model Diagram
<img width="311" height="241" alt="image" src="https://github.com/user-attachments/assets/07bdd864-40da-4daf-92f3-cc4cadc7eaad" />
---

## Security

- Uses JWT for authentication and authorization.
- Role-based access control for ADMIN, SELLER, and USER roles via Spring Security.
- Custom JWT filter and entry point to handle authentication failures.

---

## Exception Handling

- GlobalExceptionHandler provides unified API error responses.
- Custom exceptions like `ResourceNotFoundException` and `APIException` used for business logic errors.
- Validation errors are returned with detailed field-level messages.

---

## File Upload

- Products support image uploads.
- Uploaded images are saved in the server's folder with unique filenames to avoid collisions.

---

## Contributing

Contributions are welcome! Please fork the repo and submit pull requests for improvements or bug fixes.

---
