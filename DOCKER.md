# Despliegue con Docker — PsyMed Backend

Backend Spring Boot containerizado para despliegue local y publicación en **Docker Hub**.

## Requisitos

- Docker Desktop (Windows/Mac) o Docker Engine + Compose (Linux)
- Cuenta en [Docker Hub](https://hub.docker.com/) (para publicar imagen)

## Arranque local (backend + MySQL)

```powershell
cd backend
copy .env.example .env
docker compose up --build
```

Servicios:

| Servicio | URL |
|---|---|
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Health check | http://localhost:8080/actuator/health |
| MySQL | localhost:3306 (usuario `root`) |

Detener:

```powershell
docker compose down
```

Detener y borrar datos MySQL:

```powershell
docker compose down -v
```

## Construir imagen para Docker Hub

Reemplaza `TU_USUARIO` por tu usuario de Docker Hub:

```powershell
cd backend
docker login

# Build con tag para Docker Hub
docker build -t TU_USUARIO/psymed-backend:latest .

# Probar la imagen localmente (requiere MySQL accesible)
docker run --rm -p 8080:8080 ^
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/psymed?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" ^
  -e SPRING_DATASOURCE_USERNAME=root ^
  -e SPRING_DATASOURCE_PASSWORD=admin ^
  -e JWT_SECRET=mi-secreto-jwt ^
  TU_USUARIO/psymed-backend:latest
```

Publicar en Docker Hub:

```powershell
docker push TU_USUARIO/psymed-backend:latest
```

Etiquetar versión específica (recomendado):

```powershell
docker tag TU_USUARIO/psymed-backend:latest TU_USUARIO/psymed-backend:0.0.1
docker push TU_USUARIO/psymed-backend:0.0.1
```

## Desplegar en servidor (solo imagen de Docker Hub)

En el servidor de producción/staging:

```bash
docker pull TU_USUARIO/psymed-backend:latest

docker run -d --name psymed-backend \
  --restart unless-stopped \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://MYSQL_HOST:3306/psymed?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=TU_PASSWORD \
  -e JWT_SECRET=TU_JWT_SECRET_LARGO \
  -e SPRING_JPA_DDL_AUTO=update \
  TU_USUARIO/psymed-backend:latest
```

O con `docker-compose.yml` apuntando a imagen remota (sin build local):

```yaml
backend:
  image: TU_USUARIO/psymed-backend:latest
  # ... resto de environment igual que docker-compose.yml
```

## Variables de entorno

| Variable | Descripción | Default |
|---|---|---|
| `SERVER_PORT` | Puerto HTTP | `8080` |
| `SPRING_DATASOURCE_URL` | JDBC MySQL | localhost |
| `SPRING_DATASOURCE_USERNAME` | Usuario DB | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Password DB | `admin` |
| `SPRING_JPA_DDL_AUTO` | Hibernate DDL | `update` |
| `JWT_SECRET` | Secreto JWT | (cambiar en prod) |
| `JWT_EXPIRATION_DAYS` | Expiración token | `7` |

## Integración IoT (Edge Server → Backend)

Con el backend en Docker, configura el Edge Server:

```env
CENTRAL_BACKEND_URL=http://localhost:8080
```

Endpoints IoT expuestos (sin JWT, para Edge):

- `POST /api/iot/alerts`
- `POST /api/iot/daily-summary`

Endpoints Flutter (requieren JWT):

- `GET /api/patient/{id}/dashboard`
- `GET /api/patient/{id}/alerts`
- `GET /api/patient/{id}/daily-summary`

## Script rápido de publicación

```powershell
cd backend
.\scripts\docker-publish.ps1 -DockerHubUser TU_USUARIO -Tag latest
```
