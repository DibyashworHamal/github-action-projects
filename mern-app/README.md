# YouTube Clone (MERN)

This repository contains a simplified YouTube-like app built with the MERN stack.

Folders:
- `backend/` — Express + MongoDB API
- `frontend/` — React + Vite + Tailwind UI

Quick start:

1. Backend

- Create `.env` in `backend/` from `.env.example` and set `MONGO_URI` and `JWT_SECRET`.
- Install and run:

```powershell
cd backend
npm install
npm run seed   # seeds demo user and videos
npm run dev    # starts server on PORT (default 5000)
```

2. Frontend

```powershell
cd frontend
npm install
npm run dev
```

Local-only frontend (no backend APIs)
-----------------------------------

- The frontend can run completely standalone using the static data file at `frontend/public/data/videos.json`.
- Comments are stored in the browser `localStorage` using the key `comments_<videoId>`.
- If you do want to run the full MERN stack, follow the backend instructions above and/or use `docker-compose up --build`.

To run just the frontend for quick local/devops practice:

```powershell
cd frontend
npm install
npm run dev
```

Open the Vite dev URL (usually http://localhost:5173). No backend is required for browsing videos and posting comments locally.

Notes:
- This repo includes a backend API implementation, but the frontend has been adapted so it does not require it. Use the backend only if you need a real API and persistent database.
