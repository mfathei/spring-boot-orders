# Laravel ↔ Spring Boot Concept Map

## Table of Contents
1. [Project Structure & Tooling](#project-structure--tooling)
2. [Routing & Controllers](#routing--controllers)
3. [Request Handling & Validation](#request-handling--validation)
4. [Database & ORM](#database--orm)
5. [Relationships & Eager Loading](#relationships--eager-loading)
6. [Error Handling](#error-handling)
7. [Authentication & Authorization](#authentication--authorization)
8. [Testing](#testing)
9. [Events, Queues & Async](#events-queues--async)
10. [Caching, Config & Infrastructure](#caching-config--infrastructure)

---

## Project Structure & Tooling

| Laravel | Spring Boot | Notes |
|---|---|---|
| `composer.json` | `pom.xml` or `build.gradle` | Maven/Gradle are more verbose but handle build lifecycle, not just deps |
| `php artisan` | `./mvnw` or `./gradlew` | No equivalent to `artisan make:*` out of the box — IDEs fill this role |
| `.env` | `application.properties` + profiles (`application-{profile}.properties`) | Spring profiles are more powerful: entire bean graphs can swap per profile |
| `config/*.php` | `@Configuration` classes + `@Value` / `@ConfigurationProperties` | Type-safe config binding is a Spring strength |
| `app/Providers` (Service Providers) | `@Configuration` + `@Bean` methods | Both are DI registration, but Spring auto-scans by default |
| Service Container (DI) | Spring IoC Container | Constructor injection preferred in both; Spring's is compile-time safe via constructor |
| `routes/api.php` | Annotations on controller methods (`@GetMapping`, etc.) | No central route file — routes live on the controller |

## Routing & Controllers

| Laravel | Spring Boot | Notes |
|---|---|---|
| `Route::get('/users', [UserController::class, 'index'])` | `@GetMapping("/users")` on controller method | Route + handler are co-located in Spring |
| `Route::apiResource('users', UserController::class)` | No direct equivalent — define each endpoint manually | Spring HATEOAS exists but is rarely used for plain APIs |
| Route model binding (`User $user`) | `@PathVariable Long id` + manual repository lookup | Spring doesn't auto-resolve entities from URL params |
| `Route::prefix('api/v1')` | `@RequestMapping("/api/v1")` on the controller class | Same idea, different syntax |
| `Route::middleware('auth')` | `SecurityFilterChain` or `@PreAuthorize` | Auth is handled by Spring Security, not route middleware |
| `response()->json($data, 201)` | `new ResponseEntity<>(data, HttpStatus.CREATED)` | Or use `@ResponseStatus(HttpStatus.CREATED)` on the method |

## Request Handling & Validation

| Laravel | Spring Boot | Notes |
|---|---|---|
| `FormRequest` with `rules()` | Request DTO (record/class) with Bean Validation annotations | `@NotBlank`, `@Email`, `@Size`, `@Min`, `@Max`, `@Pattern` |
| `$request->validate([...])` | `@Valid` on the controller parameter | Triggers validation automatically, throws `MethodArgumentNotValidException` |
| `$request->input('name')` | `@RequestParam("name")` or fields on `@RequestBody` DTO | Spring binds JSON body to objects, query params to `@RequestParam` |
| Custom validation rules | Custom `ConstraintValidator` implementing `ConstraintValidator<A, T>` | More boilerplate than Laravel but type-safe |
| `$request->file('avatar')` | `@RequestParam MultipartFile file` | Similar concept |
| API Resources (`UserResource`) | Response DTO (record/class) | No built-in transformer — you map entity → DTO in the service or controller |
| `$request->user()` | `@AuthenticationPrincipal UserDetails user` | Or inject `SecurityContextHolder.getContext().getAuthentication()` |

## Database & ORM

| Laravel | Spring Boot (JPA/Hibernate) | Notes |
|---|---|---|
| Eloquent Model | `@Entity` class | **Key difference**: JPA entities are NOT active record. They don't query themselves — repositories do |
| `$fillable` / `$guarded` | No equivalent — use DTOs instead | Don't bind request data directly to entities |
| `$casts` | `@Enumerated`, `@Convert`, `AttributeConverter` | Type mapping is explicit via annotations |
| `$table = 'users'` | `@Table(name = "users")` | Same idea |
| `$primaryKey`, auto-increment | `@Id`, `@GeneratedValue` | Sequences are preferred over auto-increment in Spring/Postgres |
| `$timestamps` (created_at/updated_at) | `@CreationTimestamp`, `@UpdateTimestamp` | Hibernate-specific annotations |
| Soft deletes (`SoftDeletes` trait) | `@SQLDelete` + `@Where` / `@SoftDelete` (Hibernate 6.4+) | More manual than Laravel |
| Model events / Observers | `@PrePersist`, `@PostPersist`, `@EntityListeners` | JPA lifecycle callbacks |
| Query scopes | Repository query methods or `@Query` | Spring Data derives queries from method names: `findByStatusAndCreatedAtAfter(...)` |
| `DB::raw()` / raw queries | `@Query` with native SQL (`nativeQuery = true`) | Or use `JdbcTemplate` for truly raw SQL |
| `DB::transaction(fn() => ...)` | `@Transactional` on service methods | Declarative — the annotation wraps the method in a transaction |
| Migrations (`php artisan migrate`) | Flyway (`V1__name.sql`) or Liquibase | SQL-based by default, not a DSL like Laravel's Schema builder |
| Seeders | `data.sql`, `CommandLineRunner`, or test fixtures | No built-in seeder framework |
| Factories (`User::factory()`) | No built-in equivalent — use builder pattern or test helpers | Libraries like Instancio or java-faker can help |
| `->paginate(15)` | `Pageable` parameter + `Page<T>` return type | Spring Data handles pagination via query params (`?page=0&size=15&sort=id,desc`) |
| Query builder (`->where()->orderBy()`) | Specifications / Criteria API / QueryDSL | More verbose but type-safe; Specifications compose like scopes |

## Relationships & Eager Loading

| Laravel | Spring Boot (JPA) | Notes |
|---|---|---|
| `hasMany` | `@OneToMany(mappedBy = "parent")` | Bidirectional relationships need a `mappedBy` side |
| `belongsTo` | `@ManyToOne` + `@JoinColumn` | The owning side (has the FK column) |
| `belongsToMany` (pivot) | `@ManyToMany` + `@JoinTable`, or a join entity | Join entity gives you extra pivot columns |
| `hasOne` | `@OneToOne` | Watch for N+1 — lazy `@OneToOne` on the non-owning side doesn't actually lazy-load |
| `with('orders')` (eager load) | `@EntityGraph`, `join fetch` in JPQL, or `@BatchSize` | **Key difference**: JPA defaults to lazy loading. You opt into eager, not out of lazy |
| `$with = ['orders']` (always eager) | `fetch = FetchType.EAGER` | Generally discouraged in Spring — prefer per-query control |
| `withCount('orders')` | `@Formula` or a JPQL projection | No built-in equivalent |
| `load('orders')` (lazy eager load) | `Hibernate.initialize(entity.getOrders())` | Must be within a transaction/session |
| Cascade deletes | `cascade = CascadeType.ALL, orphanRemoval = true` | Same concept, annotation-based |

## Error Handling

| Laravel | Spring Boot | Notes |
|---|---|---|
| `Handler.php` / `render()` | `@RestControllerAdvice` + `@ExceptionHandler` methods | Global exception handler for all controllers |
| `abort(404)` | Throw a custom exception (e.g., `ResourceNotFoundException`) | Then catch it in the `@ControllerAdvice` |
| `abort(422, 'Validation failed')` | Automatic from `@Valid` — `MethodArgumentNotValidException` | Customize the response shape in your advice class |
| `ModelNotFoundException` | Write your own (e.g., `ResourceNotFoundException extends RuntimeException`) | Spring doesn't have a built-in "model not found" exception |
| Custom exception rendering | `@ExceptionHandler(YourException.class)` method | Return a `ResponseEntity` with your error DTO |
| `report()` (logging) | Handled in the `@ExceptionHandler` — log before returning | Or use AOP for cross-cutting logging |

## Authentication & Authorization

| Laravel | Spring Boot (Spring Security) | Notes |
|---|---|---|
| `Auth::attempt()` | `AuthenticationManager.authenticate()` | Spring Security has a full filter chain, not a simple facade |
| `Hash::make()` / `Hash::check()` | `BCryptPasswordEncoder.encode()` / `.matches()` | Register as a `@Bean` |
| `auth` middleware | `SecurityFilterChain` bean with `.authorizeHttpRequests()` | Declarative: specify which paths need auth |
| Sanctum (API tokens) | JWT (jjwt, Spring Security OAuth2 Resource Server) | No built-in token system like Sanctum — JWT is the standard approach |
| Passport (OAuth2) | Spring Authorization Server | Full OAuth2 server, much more complex |
| Gates | `@PreAuthorize("hasRole('ADMIN')")` | SpEL expressions on methods |
| Policies | Custom `PermissionEvaluator` or `@PreAuthorize` with SpEL | No direct equivalent to policy classes |
| Guards | `AuthenticationProvider` implementations | Different authentication mechanisms (DB, LDAP, OAuth) |
| `$request->user()` | `@AuthenticationPrincipal` or `SecurityContextHolder` | Inject the authenticated user into controller methods |
| `@auth` / `@guest` (Blade) | Server-side: method-level security; Client-side: include/exclude in response | Spring REST APIs don't render views — auth is pure backend |

## Testing

| Laravel | Spring Boot | Notes |
|---|---|---|
| Feature tests (`$this->getJson()`) | `MockMvc` with `@SpringBootTest` + `@AutoConfigureMockMvc` | Very similar pattern — make HTTP requests, assert responses |
| Unit tests | JUnit 5 + Mockito | `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks` |
| `RefreshDatabase` trait | `@Transactional` on test class (auto-rollback) | Each test runs in a transaction that rolls back — same effect |
| In-memory SQLite for tests | Testcontainers (spins up real Postgres in Docker) | Or H2 for simple cases, but Testcontainers is preferred for Postgres-specific features |
| `$this->actingAs($user)` | `@WithMockUser` or `SecurityMockMvcRequestPostProcessors.user()` | Test as an authenticated user |
| Factories (`User::factory()->create()`) | Builder pattern, Instancio, or `@Sql` scripts | No built-in factory system |
| Mocking (`Mockery`) | Mockito (`@MockBean` in Spring tests) | `@MockBean` replaces a bean in the Spring context for the test |
| `assertDatabaseHas()` | Query the repository in the test and assert | Or use `@Sql` to verify state |
| `$this->assertJson()` / `$this->assertJsonStructure()` | `MockMvc` + `jsonPath("$.field").value(expected)` | JSONPath assertions, very similar to Laravel's |

## Events, Queues & Async

| Laravel | Spring Boot | Notes |
|---|---|---|
| Events + Listeners | `ApplicationEvent` + `@EventListener` | Or `ApplicationEventPublisher.publishEvent()` |
| Queued listeners | `@Async` + `@EnableAsync` | For simple async. For real queues: RabbitMQ, Kafka, or SQS |
| Jobs (`dispatch(new ProcessOrder)`) | No direct equivalent — use `@Async` methods or message queues | Spring doesn't have a built-in job queue like Laravel |
| Queue workers (`php artisan queue:work`) | Consumer listeners on message brokers | Completely different paradigm — broker-based, not database-based |
| Scheduled tasks (`$schedule->command()`) | `@Scheduled(cron = "...")` + `@EnableScheduling` | Annotation-based, runs in-process |
| Broadcasting (Pusher/WebSockets) | Spring WebSocket + STOMP, or SSE | More manual setup than Laravel Broadcasting |

## Caching, Config & Infrastructure

| Laravel | Spring Boot | Notes |
|---|---|---|
| `Cache::get()` / `Cache::put()` | `@Cacheable` / `@CacheEvict` on methods | Declarative caching via annotations |
| Cache drivers (Redis, file, etc.) | `CacheManager` bean (Redis, Caffeine, EhCache) | Configure in `application.properties` |
| `config('app.name')` | `@Value("${app.name}")` | Or `@ConfigurationProperties` for type-safe groups |
| Rate limiting | `spring-boot-starter-web` + Bucket4j or Resilience4j | No built-in rate limiter like Laravel's |
| `Log::info()` | `LoggerFactory.getLogger(MyClass.class)` | SLF4J + Logback, not a facade |
| Mail (`Mail::to()->send()`) | `JavaMailSender` + `MimeMessageHelper` | More verbose, but same concept |
| File storage (`Storage::put()`) | No built-in abstraction — `java.nio.file`, or cloud SDKs directly | No equivalent to Laravel's Storage facade |
| Notifications | No built-in — implement per channel | No multi-channel notification system |
