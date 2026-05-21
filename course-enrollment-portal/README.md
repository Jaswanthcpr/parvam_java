# Course Enrollment Portal

## Prerequisites

- Java 17+
- Node.js 18+
- Docker Desktop (optional, for MySQL)

## Environment Variables

**Backend**

- `DB_URL` (example: `jdbc:mysql://localhost:3306/course_portal?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`)
- `DB_USER`
- `DB_PASS`
- `JWT_SECRET`
- `GEMINI_API_KEY`
- `SMTP_HOST`
- `SMTP_PORT`
- `SMTP_USER`
- `SMTP_PASS`
- `TWILIO_ACCOUNT_SID`
- `TWILIO_AUTH_TOKEN`
- `TWILIO_FROM_NUMBER`

## Start MySQL (Docker)

```bash
cd course-enrollment-portal
docker compose up -d
```

## Run Backend

```bash
cd course-enrollment-portal/backend
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`.

## Run Frontend

```bash
cd course-enrollment-portal/frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`.

