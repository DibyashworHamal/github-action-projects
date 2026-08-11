FROM node:20-slim AS builder

WORKDIR /app

# Copy package files and install dependencies
COPY weather-app/package.json ./
RUN npm install

# Production Stage: Minimal runtime image
FROM node:20-slim AS production

WORKDIR /app

# Copy built app and dependencies from builder stage
COPY --from=builder /app ./
COPY weather-app/ ./

# Run as non-root user for security
USER node

# Expose application port
EXPOSE 3000

# Health check using Node to ensure the server is responding correctly
HEALTHCHECK --interval=30s --timeout=3s \
  CMD node -e "require('http').get('http://127.0.0.1:3000', r=>process.exit(r.statusCode===200?0:1)).on('error',()=>process.exit(1))"

# Start application
CMD ["node", "./bin/www"]
