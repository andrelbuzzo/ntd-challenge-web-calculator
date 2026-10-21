# NTD Calculator client

React 19 client for the NTD arithmetic calculator challenge.

## Run locally

```powershell
npm install
npm start
```

The development server uses `http://localhost:3001` and proxies `/api` to the
Spring Boot API at `http://localhost:8090`.

## Available scripts

```powershell
npm start
npm test -- --watchAll=false
npm run build
```

The client requires an authenticated API session. Use the development
credentials documented in the repository root README.
