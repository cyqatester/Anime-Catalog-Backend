# Anime Catalog Backend

A REST API for managing an anime catalog, genres, and user accounts. The application is built with Java 17, Spring Boot, Spring Data JPA, PostgreSQL, and stateless JWT authentication.

## Tech stack

- Java 17
- Spring Boot 3.4.5 (Web, Security, Data JPA, Validation, Actuator)
- PostgreSQL
- Spring Security with JSON Web Tokens (JJWT 0.12.6)
- BCrypt password hashing (jBCrypt)
- Maven
- Springdoc OpenAPI / Swagger UI
- JUnit 5 and Spring Boot Test
- GitHub Actions

## Features

- Login with username and password; issue signed, expiring JWT access tokens.
- Role-based access for `ADMIN` and `STANDARD` users.
- Anime CRUD with filtering by title, genre, airing year, and release category.
- Genre CRUD and name search.
- Admin user management, including user search.
- Current-user profile, password change, and admin-only username change.
- BCrypt-hashed passwords; user API responses do not expose password hashes.
- Database schema updates managed by Hibernate.

## Requirements

- JDK 17+
- Maven 3.8+ (or use the Maven wrapper if one is added to the project)
- PostgreSQL 13+ (a compatible supported PostgreSQL release is sufficient)

## Setup

### 1. Create the database

Create a PostgreSQL database:

```sql
CREATE DATABASE anime_catalog;
```

The checked-in `src/main/resources/application.yml` currently configures:

| Setting | Default |
|---|---|
| JDBC URL | `jdbc:postgresql://localhost:5432/anime_catalog` |
| Database username | `postgres` |
| Database password | `admin` |
| Application port | `8080` |

Change the database credentials in `application.yml` for your local setup, or override them with Spring Boot environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/anime_catalog
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your_database_password
```

Do not commit real database credentials.

### 2. Configure JWT

The application reads its signing key and token lifetime from these properties:

| Environment variable | Property | Default |
|---|---|---|
| `JWT_SECRET` | `app.jwt.secret` | Local development key in `application.yml` |
| `JWT_EXPIRATION_MS` | `app.jwt.expiration-ms` | `86400000` (24 hours) |

Set a unique, random signing secret of at least 32 bytes outside local development:

```bash
export JWT_SECRET='replace-with-a-long-random-secret-of-at-least-32-bytes'
export JWT_EXPIRATION_MS=86400000
```

The application rejects a JWT signing secret shorter than 32 bytes. Never use the checked-in development default in production.

### 3. Build, test, and run

```bash
mvn clean verify
mvn spring-boot:run
```

The API listens on [http://localhost:8080](http://localhost:8080).

### 4. Open API documentation

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Authentication and authorization

All protected API requests use a bearer access token. Obtain one by logging in:

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "superadmin",
  "password": "ChangeMe123!"
}
```

A successful response contains the signed token and basic user information:

```json
{
  "token": "<jwt-access-token>",
  "user": {
    "username": "superadmin",
    "name": "Super Administrator",
    "role": "ADMIN"
  }
}
```

Send the token on subsequent protected requests:

```http
Authorization: Bearer <jwt-access-token>
```

Tokens are stateless, signed with the configured JWT secret, and expire after the configured lifetime (24 hours by default). A missing, invalid, or expired token cannot access protected routes.

### Default administrator

On startup, the application creates the following administrator if the username does not already exist:

- Username: `superadmin`
- Password: `ChangeMe123!`
- Role: `ADMIN`

The password is stored as a BCrypt hash. Change the default password immediately after the first login. The seeded super-administrator account cannot be deleted through the user-delete endpoint.

### Roles

| Capability | STANDARD | ADMIN |
|---|:---:|:---:|
| Search/list anime | Yes | Yes |
| Search/list genres | Yes | Yes |
| Create, update, or delete anime | No | Yes |
| Create, update, or delete genres | No | Yes |
| List/search and manage users | No | Yes |
| View own profile | Yes | Yes |
| Change own password | Yes | Yes |
| Change own username | No | Yes |

The API uses Spring Security for JWT authentication and role checks. Controller-level checks also enforce admin-only operations.

## API reference

All routes below are relative to `http://localhost:8080`. Except for login, protected routes require the `Authorization: Bearer <token>` header.

### Authentication

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/login` | Public | Validate credentials and return a JWT |

Login request:

```json
{
  "username": "superadmin",
  "password": "ChangeMe123!"
}
```

### Anime

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/anime` | STANDARD, ADMIN | List/filter anime |
| POST | `/api/anime` | ADMIN | Create an anime entry |
| PUT | `/api/anime/{id}` | ADMIN | Update an anime entry |
| DELETE | `/api/anime/{id}` | ADMIN | Delete an anime entry |

The list endpoint accepts optional query parameters:

| Parameter | Type | Behavior |
|---|---|---|
| `title` | String | Case-insensitive partial match against title or alternate title |
| `genre` | String | Case-insensitive exact genre-name match |
| `yearAired` | Integer | Exact airing-year match |
| `releaseCategory` | Enum | Exact category match: `TV_SERIES`, `OVA`, or `MOVIE` |

Filters can be combined. For example:

```http
GET /api/anime?title=cowboy&genre=Action&yearAired=1998&releaseCategory=TV_SERIES
```

Create/update request body:

```json
{
  "title": "Cowboy Bebop",
  "alternateTitle": "カウボーイビバップ",
  "genreIds": [1, 2],
  "yearAired": 1998,
  "releaseCategory": "TV_SERIES",
  "episodes": 26,
  "season": "Spring 1998"
}
```

Required fields: `title`, `alternateTitle`, a non-empty `genreIds` array containing existing genre IDs, `yearAired`, and `releaseCategory`. The year must be between 1900 and 2100. `episodes` and `season` are optional; when supplied, episodes must be at least 1.

### Genres

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/genres` | STANDARD, ADMIN | List all genres |
| GET | `/api/genres?search=action` | STANDARD, ADMIN | Search genre names (case-insensitive partial match) |
| POST | `/api/genres` | ADMIN | Create a genre |
| PUT | `/api/genres/{id}` | ADMIN | Rename a genre |
| DELETE | `/api/genres/{id}` | ADMIN | Delete a genre |

Create/update request body:

```json
{
  "name": "Action"
}
```

Genre names are required and unique; the database column allows up to 80 characters.

### User administration

All endpoints in this section require the `ADMIN` role.

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/users` | List users |
| GET | `/api/admin/users?search=sample` | Search by name or username (case-insensitive partial match) |
| POST | `/api/admin/users` | Create a user |
| PUT | `/api/admin/users/{id}` | Update a user |
| DELETE | `/api/admin/users/{id}` | Delete a user |

Create/update request body:

```json
{
  "name": "Sample User",
  "username": "sample",
  "password": "ChangeThis123!",
  "role": "STANDARD"
}
```

Allowed roles are `STANDARD` and `ADMIN`. The API returns user views containing `id`, `name`, `username`, `role`, and `superAdmin`; password hashes are never included. An administrator cannot delete their own account or the seeded super-administrator.

### Current user

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/me` | STANDARD, ADMIN | Return the authenticated user's profile |
| PUT | `/api/me/password` | STANDARD, ADMIN | Change the authenticated user's password |
| PUT | `/api/me/username` | ADMIN | Change the authenticated user's username |

Change password request:

```json
{
  "newPassword": "A new strong password"
}
```

Change username request:

```json
{
  "username": "new-admin-name"
}
```

The profile response contains `name`, `username`, and `role`.

## Data model

- **Anime**: title, alternate title, airing year, release category, optional episode count and season.
- **Genre**: unique genre name.
- **UserAccount**: name, unique username, BCrypt password hash, role, and super-administrator flag.
- **Anime–Genre**: many-to-many relationship stored in the `anime_genres` join table.

Hibernate is configured with `ddl-auto: update`, so it updates the schema automatically at application startup. For production deployments, use a managed schema migration tool and review schema changes rather than relying on automatic updates.

## Testing and continuous integration

Run the full Maven verification lifecycle locally:

```bash
mvn clean verify
```

GitHub Actions runs the same `mvn --batch-mode clean verify` command on pushes and pull requests targeting `main` or `master`. The workflow uses Ubuntu 24.04, Temurin Java 17, and Maven dependency caching.

## Operational and security notes

- Use HTTPS in deployed environments and keep JWT signing secrets and database credentials outside source control.
- Replace the default administrator password immediately.
- Configure a strong, unique `JWT_SECRET` (at least 32 bytes); rotate it deliberately because tokens signed with the previous key will no longer validate.
- The JWT contains the username as its subject and the user's role as a claim. Keep token lifetimes appropriate for the deployment.
- Passwords are hashed with BCrypt; never store or log plaintext passwords.
- CORS is configured separately in `CorsConfig`; adjust allowed origins to match the frontend deployment.
- Actuator web exposure is configured for the `mappings` endpoint. Review management endpoint exposure before deployment.
- Use least-privilege database credentials and back up the PostgreSQL database regularly.

## Project structure

```text
src/
├── main/
│   ├── java/com/example/animecatalog/
│   │   ├── config/       # Security, JWT filter, CORS, data initialization
│   │   ├── controller/   # Authentication, anime, genre, user, profile APIs
│   │   ├── entity/       # JPA entities and enums
│   │   ├── repository/   # Spring Data repositories and anime search
│   │   └── service/      # Actor, password, and token services
│   └── resources/
│       └── application.yml
└── test/                 # Automated tests
```

## License

No license file is currently included in this repository. Add a `LICENSE` file if you intend to publish reuse or distribution terms.
