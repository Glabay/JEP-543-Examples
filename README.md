# Midnight Dashboard

A small developer-directory and progression dashboard for **The Midnight Network**. It includes a Spring MVC/Thymeleaf interface, a PostgreSQL database, 36 fictional seed developers, and a REST API for reading and updating developer records.

The dashboard summary is also a practical **JEP 543 – Structured Concurrency** example: independent PostgreSQL aggregate queries are forked into a `StructuredTaskScope`, joined together, and cancelled as a group if one task fails.

## Stack

- Java 28 Early Access (JEP 543 is final in JDK 28; no preview flags are required)
- Spring Boot 4.2.0-RC1
- Maven
- Spring MVC + Thymeleaf
- JDBC + PostgreSQL 18
- Flyway database migrations and mock-data seeding
- Docker Compose for local PostgreSQL

> **Compatibility note:** JDK 28 is still an Early Access build. Spring Boot 4.2.0-RC1 is a pre-release, and the current published Spring Boot system-requirements page for the stable 4.1.2 line lists compatibility through Java 27. This project intentionally uses the JDK 28 Structured Concurrency API, so treat the JDK 28 + Spring Boot 4.2 RC combination as an experimental learning setup, not a production-supported stack. See the official documentation linked below.

## Start locally

### 1. Start PostgreSQL

From the project root:

```powershell
docker compose up -d
```

The database is exposed on **localhost:6942** and persists in the `midnight-dashboard-postgres-data` Docker volume.

Default local connection details:

| Setting | Value |
|---|---|
| Host | `localhost` |
| Port | `6942` |
| Database | `midnight_dashboard` |
| Username | `midnight` |
| Password | `midnight-local-only` |

These are demo credentials only. Change them in a local `.env` file before using this outside your machine. Copy `.env.example` to `.env`; Compose reads it automatically, and Spring Boot imports it as a properties file.

Wait until the container reports healthy:

```powershell
docker compose ps
```

### 2. Run the Spring Boot application

Use a JDK 28 Early Access installation and Maven 3.9 or newer:

```powershell
java -version
mvn -version
mvn spring-boot:run
```

Or open the folder in IntelliJ IDEA, set the Project SDK and Maven runner JDK to JDK 28, then run `MidnightDashboardApplication`.

Flyway creates the `developers` table and inserts the 36 sample profiles on the first successful application start.

### 3. Open the dashboard

- Dashboard: <http://localhost:8080>
- Developer API: <http://localhost:8080/api/v1/developers>
- Structured Concurrency summary API: <http://localhost:8080/api/v1/dashboard/summary>

The PostgreSQL host port is **6942**; the Spring Boot web application listens on **8080**.

## Docker Compose commands

```powershell
# Start PostgreSQL
docker compose up -d

# View database status and health
docker compose ps

# Follow PostgreSQL logs
docker compose logs -f postgres

# Stop PostgreSQL but keep the data volume
docker compose down

# Stop PostgreSQL and delete the local database volume (resets the demo data)
docker compose down -v
```

## What is included

- Overview cards for total developers, active developers, average progress, and represented levels.
- Developer distribution across Fresher, Intern, Junior, Mid-level, Senior, Staff, and Principal.
- Team distribution across Platform, Backend, Frontend, Full Stack, DevOps, Mobile, Data, Quality Engineering, and Security.
- Search and client-side filters for level, team, and status.
- Individual developer profile pages with mentor, team, experience, join date, and current-level progress.
- Read, create, and progression-update REST endpoints.
- 36 fictional records using reserved `example.test` email addresses and non-production GitHub-style handles.

The progress percentage is a mock field for demonstration. It should not be treated as an objective performance score. A real implementation should define transparent competencies and review progression with the developer and mentor.

## REST API

### List developers

```http
GET /api/v1/developers
```

Optional query parameters: `level`, `team`, `status`, `search`.

Example:

```http
GET /api/v1/developers?level=JUNIOR&team=BACKEND&status=ACTIVE
GET /api/v1/developers?search=alex
```

### Read a developer

```http
GET /api/v1/developers/{id}
```

### Create a developer

```http
POST /api/v1/developers
Content-Type: application/json
```

Example body:

```json
{
  "firstName": "Alex",
  "lastName": "Example",
  "email": "alex.example@example.test",
  "githubUsername": "alex-example",
  "level": "FRESHER",
  "team": "BACKEND",
  "status": "ACTIVE",
  "progressPercent": 15,
  "experienceYears": 0.0,
  "joinedOn": "2026-11-04",
  "mentorId": 7,
  "bio": "Learning Java and backend development."
}
```

`mentorId` can be omitted or set to `null`. If supplied, it must reference an existing developer.

### Update developer level and progress

```http
PATCH /api/v1/developers/{id}/progress
Content-Type: application/json
```

Example body:

```json
{
  "level": "JUNIOR",
  "progressPercent": 68
}
```

The update also refreshes the developer's `last_active_at` timestamp.

### Get dashboard aggregates

```http
GET /api/v1/dashboard/summary
```

This endpoint uses JEP 543. `DashboardService` starts seven independent queries in child virtual threads:

1. Total developer count.
2. Active developer count.
3. Average progress.
4. Count grouped by development level.
5. Count grouped by team.

The parent task calls `join()` before reading results. The default joiner waits for all subtasks to succeed and cancels unfinished sibling tasks if one fails. Each query uses its own JDBC connection from the Hikari connection pool.

## Development levels

| Enum value | Display label |
|---|---|
| `FRESHER` | Fresher |
| `INTERN` | Intern |
| `JUNIOR` | Junior |
| `MID_LEVEL` | Mid-level |
| `SENIOR` | Senior |
| `STAFF` | Staff |
| `PRINCIPAL` | Principal |

## Project layout

```text
midnight-dashboard/
├── compose.yaml
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/dev/midnightcoder/midnightdashboard/
        │   ├── MidnightDashboardApplication.java
        │   ├── api/
        │   ├── dashboard/
        │   └── developer/
        └── resources/
            ├── application.yml
            ├── db/migration/
            ├── static/css/
            ├── static/js/
            └── templates/
```

## Useful references

- [JEP 543 – Structured Concurrency](https://openjdk.org/jeps/543)
- [JDK 28 Early Access API – StructuredTaskScope](https://download.java.net/java/early_access/jdk28/docs/api/java.base/java/util/concurrent/StructuredTaskScope.html)
- [Spring Boot system requirements](https://docs.spring.io/spring-boot/system-requirements.html)
- [Spring Boot 4.2.0-RC1 announcement](https://spring.io/blog/2026/10/23/spring-boot-4-2-0-RC1-available-now/)
- [PostgreSQL versioning policy](https://www.postgresql.org/support/versioning/)
