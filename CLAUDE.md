# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Test Commands

- **Build / Compile**: `./mvnw clean compile`
- **Package application**: `./mvnw clean package`
- **Run the application**: `./mvnw spring-boot:run`
- **Run all tests**: `./mvnw test`
- **Run single test class**: `./mvnw test -Dtest=OrdersApplicationTests`
- **Run single test method**: `./mvnw test -Dtest=OrdersApplicationTests#contextLoads`
- **Build skipping tests**: `./mvnw clean package -DskipTests`

*Note: Tests currently execute `@SpringBootTest` which requires a running PostgreSQL instance on `localhost:5432` with credentials defined in `application.properties`.*

## High-Level Architecture & Project Structure

- **Framework**: Spring Boot 4.1.x, Java 21, Spring Data JPA, Spring Web MVC.
- **Database & Migrations**:
  - PostgreSQL runtime driver with Flyway migrations (`src/main/resources/db/migration/`).
  - Hibernate DDL mode is set to `validate` (`spring.jpa.hibernate.ddl-auto=validate`), meaning database schema modifications must be done via Flyway SQL migration files (`V{n}__*.sql`) rather than automatic JPA generation.
- **Domain Model Relationships (`orders.orders.model`)**:
  - `User`: Has many `Order` instances (`cascade = ALL, orphanRemoval = true`).
  - `Order`: Belongs to `User` (Lazy fetch, `user_id` FK). Has many `OrderItem` instances (`cascade = ALL, orphanRemoval = true`). Includes an `OrderStatus` enum (`PENDING`, `COMPLETED`, `CANCELED`).
  - `OrderItem`: Join entity linking `Order` and `Product` (Lazy fetch on both, with unique constraint across `order_id` and `product_id`).
  - `Product`: Has many `OrderItem` instances. Tracks stock `quantity` and pricing.
- **Layered Structure**:
  - **Controllers** (`orders.orders.controller`): Exposes REST APIs under `/api/v1/users` and `/api/v1/orders`. Supports Spring Data pagination and sorting via `@PageableDefault Pageable`.
  - **Services** (`orders.orders.service`): Annotated with `@Transactional(readOnly = true)` at the class level; write operations explicitly declare `@Transactional`.
  - **Repositories** (`orders.orders.repository`): Spring Data JPA repositories. `OrderRepository` demonstrates N+1 query solutions using both `left join fetch` and `@EntityGraph(attributePaths = "orderItems")`.
- **API Testing**:
  - HTTP scratch files are located in `http/` (`http/users.http` and `http/orders.http`) for manual testing with IntelliJ HTTP Client.
