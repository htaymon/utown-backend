# UTown Backend

A Spring Boot REST API for a campus food ordering and delivery platform. Restaurants manage menus and orders, while students browse restaurants, place orders, manage delivery addresses, and track deliveries through a secure JWT-authenticated system.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8-blue?logo=mysql)
![Maven](https://img.shields.io/badge/Build-Maven-red?logo=apachemaven)

---

## Highlights

* Designed and implemented 16 REST controllers
* Built JWT-based authentication and authorization using Spring Security
* Developed shopping cart and order management workflows
* Implemented role-based access control for administrators and customers
* Integrated Swagger/OpenAPI documentation
* Applied layered architecture (Controller → Service → Repository)
* Deployed to Railway for public access

---

## Tech Stack

| Category      | Technology                 |
| ------------- | -------------------------- |
| Language      | Java 17                    |
| Framework     | Spring Boot                |
| Security      | Spring Security, JWT       |
| Database      | MySQL                      |
| ORM           | Spring Data JPA, Hibernate |
| Mapping       | MapStruct                  |
| Documentation | Swagger / OpenAPI          |
| Build Tool    | Maven                      |
| Deployment    | Railway                    |

---

## Features

### Authentication & Authorization

* User registration and login
* JWT token generation and validation
* Role-based access control
* BCrypt password hashing

### Restaurant & Menu Management

* Restaurant management
* Restaurant categories
* Dish management
* Dish categories
* Dish options and add-ons

### Cart & Orders

* Shopping cart management
* Order creation
* Order status tracking
* Order history
* Ownership validation

### Delivery

* Delivery addresses
* Delivery areas
* Delivery status tracking

### Administration

* User management
* Role management
* Staff scheduling
* Notifications

---

## Architecture

```text
Client
   ↓
Controller Layer
   ↓
Service Layer
   ↓
Repository Layer
   ↓
MySQL Database
```

---

## Project Structure

```text
src/main/java/com/utown/utown_backend
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── mapper
├── repository
├── security
└── service
```

---

## API Documentation

### Production

**Live API**

https://utown-backend-production-3238.up.railway.app

**Swagger UI**

https://utown-backend-production-3238.up.railway.app/swagger-ui/index.html

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

---

## Prerequisites

Before running the application, make sure the following software is installed:

* Java 17+
* Maven 3.9+
* MySQL 8+

---

## Database Setup

Create a MySQL database:

```sql
CREATE DATABASE utown;
```

You can use an existing MySQL user or create a dedicated user for the project.

Example:

```sql
CREATE USER 'utown_user'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON utown.* TO 'utown_user'@'%';
FLUSH PRIVILEGES;
```

---

## Environment Variables

The application uses environment variables to keep sensitive information such as database credentials and JWT secrets out of source control.

Create a `.env` file in the project root using `.env.example` as a template.

Example:

```env
DATABASE_URL=jdbc:mysql://localhost:3306/utown?user=root&password=your_password&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC

APP_JWT_SECRET=your-jwt-secret-at-least-32-characters-long

APP_JWT_EXPIRATION_MS=86400000

CORS_ALLOWED_ORIGINS=http://localhost:3000
```

### Environment Variables Description

| Variable              | Description                                                |
| --------------------- | ---------------------------------------------------------- |
| DATABASE_URL          | MySQL database connection URL                              |
| APP_JWT_SECRET        | Secret key used to sign JWT tokens (minimum 32 characters) |
| APP_JWT_EXPIRATION_MS | JWT expiration time in milliseconds                        |
| CORS_ALLOWED_ORIGINS  | Allowed frontend origins                                   |

---

## Running from IntelliJ IDEA

1. Open the project in IntelliJ IDEA.
2. Open **Run → Edit Configurations**.
3. Select your Spring Boot configuration.
4. Add the following environment variables:

```text
DATABASE_URL=jdbc:mysql://localhost:3306/utown?user=root&password=your_password&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC;
APP_JWT_SECRET=your-jwt-secret-at-least-32-characters-long;
APP_JWT_EXPIRATION_MS=86400000;
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

5. Save the configuration.
6. Run the application.

---

## Running from Terminal

Export the required environment variables:

```bash
export DATABASE_URL="jdbc:mysql://localhost:3306/utown?user=root&password=your_password&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"

export APP_JWT_SECRET="your-jwt-secret-at-least-32-characters-long"

export APP_JWT_EXPIRATION_MS=86400000

export CORS_ALLOWED_ORIGINS="http://localhost:3000"
```

Start the application:

```bash
mvn spring-boot:run
```

---

## Build Project

```bash
mvn clean install
```

---

## Run Tests

Before running tests, make sure the same environment variables are available.

```bash
mvn test
```

---

## Application URLs

After the application starts successfully:

### API Base URL

```text
http://localhost:8080
```

### Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI Specification

```text
http://localhost:8080/v3/api-docs
```

---

## Future Improvements

* Unit testing with JUnit and Mockito
* Integration testing with Testcontainers
* Docker support
* GitHub Actions CI/CD
* Redis caching
* Flyway database migrations
* Spring Boot Actuator monitoring

---

## License

This project is provided for portfolio and educational purposes.

---

## Author

**Htay Htay Mon**

Backend Developer specializing in Java, Spring Boot, and REST API development.

* GitHub: https://github.com/htaymon
