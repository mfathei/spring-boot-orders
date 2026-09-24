# Repository Guidelines

## Project Structure & Module Organization
This repository is a Spring Boot application (Java 21, Spring Data JPA) for managing users, products, and orders.
- `src/main/java/orders/orders/`: Root Java package.
  - `controller/`: REST API controllers (`/api/v1/users`, `/api/v1/orders`).
  - `service/`: Business logic and transaction management (`@Transactional`).
  - `repository/`: Spring Data JPA repositories with custom fetch join queries.
  - `model/`: JPA entities (`User`, `Order`, `OrderItem`, `Product`, `OrderStatus`).
- `src/main/resources/db/migration/`: Flyway SQL migration scripts (`V{n}__*.sql`).
- `src/test/java/orders/orders/`: JUnit test suite.
- `http/`: HTTP scratch files (`users.http`, `orders.http`) for manual API testing.

## Build, Test, and Development Commands
Use the Maven Wrapper (`./mvnw`) to build, run, and test locally:
- `./mvnw clean compile`: Compile source files.
- `./mvnw clean package`: Package the runnable JAR file.
- `./mvnw spring-boot:run`: Run the Spring Boot application locally on port 8080.
- `./mvnw test`: Execute all unit and integration tests.
- `./mvnw test -Dtest=OrdersApplicationTests`: Run a specific test class.
- `./mvnw test -Dtest=OrdersApplicationTests#contextLoads`: Run a specific test method.

## Coding Style & Naming Conventions
- **Language & Formatting**: Java 21 syntax. Use 4 spaces for Java code indentation; 2 spaces for XML, YAML, and SQL files.
- **Naming Rules**: Use `PascalCase` for classes and interfaces, `camelCase` for fields and methods, and `UPPER_SNAKE_CASE` for constants and enum values (e.g., `OrderStatus.PENDING`).
- **Database Migrations**: Hibernate schema generation is set to `validate`. All schema modifications must be applied through Flyway migration scripts under `src/main/resources/db/migration/` using versioned naming (`V{n}__description.sql`).
- **Transactions**: Annotate service classes with `@Transactional(readOnly = true)` at class level and explicitly declare `@Transactional` on mutation methods.

## Testing Guidelines
- **Frameworks**: Built with JUnit 5, Spring Boot Test (`@SpringBootTest`), and Mockito.
- **Environment**: Integration tests require a running PostgreSQL instance on `localhost:5432` matching credentials in `application.properties`.
- **Naming**: Test classes must end with `Tests.java` or `Test.java` and mirror the package location of the target source class.

## Commit & Pull Request Guidelines
- **Commit Messages**: Follow Conventional Commits formatting (`<type>(<scope>): <summary>` or `<type>: <summary>`).
  - *Types*: `feat`, `fix`, `refactor`, `config`, `docs`, `test`.
  - *Example*: `feat(orders): add fetch join query for order items`
- **Pull Requests**: Include a clear summary of changes, linked issues, and verify that `./mvnw test` passes locally before submitting.
