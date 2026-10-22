# Chano

Two independent projects:

```text
backend/     Node 24 + TypeScript + Fastify API
app/         Compose Multiplatform + Android and SwiftUI iOS launchers
```

Start the API:

```sh
cd backend
npm ci
cp .env.example .env
npm run dev
```

In a second terminal, start the desktop app:

```sh
cd app
./gradlew :composeApp:run
```

The starter screen calls `GET /api/v1/info` and shows loading, connected, or
unavailable states. Refresh works from the Compose button or the native iOS control.

See [backend/README.md](backend/README.md) and [app/README.md](app/README.md)
for configuration, platform launch commands, and verification.
