FROM node:26-alpine
WORKDIR /app

# Copy package.json and package-lock.json first to leverage Docker cache
COPY package*.json ./

# Install dependencies (npm ci is better for reliable builds)
RUN npm ci

# Copy the rest of the backend source code
COPY . .

# Expose the port the app runs on (matching your .env.example)
EXPOSE 5000

# Command to run the application
CMD ["npm", "start"]