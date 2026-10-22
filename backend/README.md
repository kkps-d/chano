# Chano backend

Requires Node 24 LTS. From this folder:

```sh
npm ci
cp .env.example .env
npm run dev
```

The server listens on port 3000. `GET /health` returns `{ "status": "ok" }`;
`GET /api/v1/info` returns the starter app's name, version, and connection message.

```sh
npm run check  # strict types, route/config/CORS tests, production build
npm start     # run dist/server.js after building
```

Configuration is validated at startup. Set `CORS_ORIGINS` to exact browser origins
(comma separated). Native clients do not require CORS. Use HTTPS when deploying.
No database, authentication, or product-specific endpoints have been chosen yet.
