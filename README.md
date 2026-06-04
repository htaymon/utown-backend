# UTown Backend

A Spring Boot-based e-commerce backend application that provides secure RESTful APIs for product management, shopping cart operations, order processing, delivery management, and user authentication.

---

## Overview

UTown Backend is designed using a layered architecture to ensure maintainability, scalability, and clean separation of concerns.

The project demonstrates backend engineering skills including:

* REST API development
* Spring Security implementation
* Database design and integration
* DTO and Entity mapping
* Exception handling
* Structured logging
* Software maintenance and debugging

---

## Features

### Authentication & Security

* User Registration
* User Login
* Role-Based Authorization
* Spring Security Configuration
* Protected REST APIs

### Shopping Features

* Product Management
* Shopping Cart Management
* Cart Item Operations
* Order Processing
* Order Status Tracking
* Delivery Address Management

### Backend Engineering

* RESTful API Development
* DTO Pattern
* Entity Mapping
* Input Validation
* Exception Handling
* Structured Logging
* Database Integration

---

## Technology Stack

### Backend

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA

### Database

* MySQL

### API Documentation

* Swagger / OpenAPI

### Build Tool

* Maven

### Version Control

* Git
* GitHub

---

## Architecture

```text
Client
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
MySQL Database
```

### Project Structure

```text
src/main/java
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── mapper
├── repository
├── security
├── service
└── UtownBackendApplication
```

---

## API Documentation

Swagger/OpenAPI documentation is available after running the application.

### Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI Specification

```text
http://localhost:8080/v3/api-docs
```

Swagger provides:

* API endpoint documentation
* Request and response examples
* Parameter descriptions
* HTTP status codes
* Interactive API testing

---

## Key Modules

### Authentication Module

* User Registration
* User Login
* Authorization

### Product Module

* Product Creation
* Product Update
* Product Retrieval
* Product Management

### Cart Module

* Add Item to Cart
* Update Cart Quantity
* Remove Cart Item
* View Cart Details

### Order Module

* Create Order
* View Order History
* Update Order Status

### Delivery Module

* Manage Delivery Address
* Delivery Information Management

---

## Error Handling

The application includes centralized exception handling for:

* Validation Errors
* Resource Not Found Errors
* Authentication Failures
* Business Logic Exceptions
* Internal Server Errors

---

## Development Highlights

* Designed and implemented RESTful APIs using Spring Boot
* Applied layered architecture principles
* Implemented Spring Security configuration
* Developed shopping cart and order management modules
* Performed debugging and defect analysis
* Improved logging and exception handling
* Implemented DTO mapping and entity relationships
* Maintained and enhanced backend services

---

## Contributions

Key responsibilities during development:

* Backend feature implementation
* Functional testing and validation
* Defect analysis and debugging
* API development and maintenance
* Database integration
* Security configuration
* Code maintenance and refactoring

---

## Getting Started

### Clone Repository

```bash
git clone https://github.com/htaymon/utown-backend.git
```

### Navigate to Project

```bash
cd utown-backend
```

### Build Project

```bash
mvn clean install
```

### Run Application

```bash
mvn spring-boot:run
```

The application will start on:

```text
http://localhost:8080
```

---

## Future Improvements

* Docker Deployment
* Unit Testing with JUnit & Mockito
* CI/CD using GitHub Actions
* Redis Caching
* Payment Gateway Integration
* Performance Optimization

---

## Author

**Htay Htay Mon**

Backend Developer

GitHub Repository:
https://github.com/htaymon/utown-backend
