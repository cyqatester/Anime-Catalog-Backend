# Anime Catalog Backend

Java 17 · Maven · Spring Boot 3.4.5 · PostgreSQL · Spring Data JPA · JUnit 5 · Swagger UI · GitHub Actions

## Important security limitation
This project intentionally does **not** include Spring Security or an authentication mechanism. To demonstrate role-specific behavior, protected endpoints accept `X-Username` and look up that account in PostgreSQL. This is only a development/demo gate: any caller can spoof the header. Do not expose this API to the internet or use it with real user data until proper authentication and authorization are added. Passwords are BCrypt-hashed using jBCrypt.

## 1. Database
Create a PostgreSQL database named `anime_catalog`. Default connection is `localhost:5432`, user `postgres`, password `postgres`. Override with `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables. Hibernate creates/updates tables on startup.

## 2. Run
```bash
mvn clean test
mvn spring-boot:run
```
Swagger: http://localhost:8080/swagger-ui/index.html

<img width="824" height="736" alt="image" src="https://github.com/user-attachments/assets/cc6eab7f-0079-4f72-ab04-044b153815ff" />

A default protected account is created if missing: username `superadmin`, password `ChangeMe123!`. Change this password before using the application. It cannot be deleted through the user-delete endpoint.

## 3. API
All API endpoints require `X-Username: <existing username>` except none are public. Example header: `X-Username: superadmin`. This is not authentication.

| Method | Endpoint | Role | Purpose |
|---|---|---|---|
| GET | `/api/anime` | Standard/Admin | List/search anime; optional `title`, `genre`, `yearAired`, `releaseCategory` |
| POST | `/api/anime` | Admin | Create anime |
| PUT | `/api/anime/{id}` | Admin | Update anime |
| DELETE | `/api/anime/{id}` | Admin | Delete anime |
| GET | `/api/genres?search=...` | Standard/Admin | List/search genres |
| POST/PUT/DELETE | `/api/genres[/id]` | Admin | Genre CRUD |
| GET | `/api/admin/users?search=...` | Admin | List/search users |
| POST/PUT/DELETE | `/api/admin/users[/id]` | Admin | User CRUD |
| GET | `/api/me` | Standard/Admin | View own name, username, role |
| PUT | `/api/me/password` | Standard/Admin | Change own password |
| PUT | `/api/me/username` | Admin | Change own username |

Anime JSON example:
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
User JSON example:
```json
{ "name": "Sample User", "username": "sample", "password": "ChangeThis123!", "role": "STANDARD" }
```
Release category values: `TV_SERIES`, `OVA`, `MOVIE`; user roles: `STANDARD`, `ADMIN`. User list responses never expose password hashes.

## 4. GitHub Actions
`.github/workflows/ci.yml` runs `mvn clean verify` on pushes and pull requests to `main`/`master`.

## 5. Notes
- Anime must have a title, alternate title, at least one existing genre, year aired, and release category. Episodes and season are optional.
- Deleting the superadmin and deleting the currently acting admin are blocked.
- Genre deletion may fail if a genre is still referenced by anime; remove its associations first.
- For production, add authentication and authorization before deployment; do not mistake `X-Username` for a secure identity mechanism.
