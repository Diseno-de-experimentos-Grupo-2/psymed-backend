# Docker — PsyMed Backend (Neon Postgres)

Run **only the Spring Boot backend** in Docker. The database lives on **[Neon](https://neon.tech)** (serverless Postgres). Inspect data with the **Neon VS Code extension** or any Postgres:PG client.

## Prerequisites

- Docker Desktop
- A Neon project ([console.neon.tech](https://console.neon.tech))
- Backend `.env` with Neon JDBC credentials

## 1. Neon setup

1. Create a project/database in the Neon console.
2. Open **Connection details** → choose **JDBC** (or copy host, database, user, password).
3. Build the URL (SSL is required):

```text
jdbc:postgresql://<host>/<database>?sslmode=require
```

Example:

```text
jdbc:postgresql://ep-cool-name-123456.us-east-2.aws.neon.tech/neondb?sslmode=require
```

## 2. Backend `.env`

```powershell
cd backend
copy .env.example .env
```

Edit `.env` and set:

| Variable | Source |
|---|---|
| `SPRING_DATASOURCE_URL` | Neon JDBC URL (with `?sslmode=require`) |
| `SPRING_DATASOURCE_USERNAME` | Neon user |
| `SPRING_DATASOURCE_PASSWORD` | Neon password |

On first start, Hibernate `ddl-auto=update` creates/updates tables. Seeders add demo users (`dina`/`dinadina`, `pro`/`propropro`) and IoT device `ESP32_001` (pairing code `123456`).

## 3. Start backend

```powershell
cd backend
docker compose up --build
```

| Service | URL |
|---|---|
| API | http://localhost:8080 |
| Swagger | http://localhost:8080/swagger-ui.html |
| Health | http://localhost:8080/actuator/health |

Stop:

```powershell
docker compose down
```

## 4. VS Code — Neon extension

1. Install the **[Neon](https://marketplace.visualstudio.com/items?itemName=neon-com.neon)** extension (or **PostgreSQL** / **SQLTools** with a Postgres driver).
2. Sign in to Neon or paste the connection string from the Neon dashboard.
3. Browse tables (`account`, `patient_profile`, `iot_device`, etc.) after the backend has started once.

Neon console also has a built-in SQL editor if you prefer the web UI.

## 5. Edge Server (IoT)

Point the Edge at the Docker backend on your host:

```env
CENTRAL_BACKEND_URL=http://localhost:8080
```

## 6. Docker Hub (optional)

```powershell
docker login
docker build -t TU_USUARIO/psymed-backend:latest .
docker push TU_USUARIO/psymed-backend:latest
```

Use the same Neon env vars when running on Render or any host — only the backend container is deployed; Postgres stays on Neon.

## Environment reference

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | Neon JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | Neon user |
| `SPRING_DATASOURCE_PASSWORD` | Neon password |
| `SPRING_JPA_DDL_AUTO` | Default `update` |
| `JWT_SECRET` | JWT signing secret |
| `CORS_ALLOWED_ORIGINS` | Frontend origin(s) |
| `BACKEND_PORT` | Host port (default `8080`) |
