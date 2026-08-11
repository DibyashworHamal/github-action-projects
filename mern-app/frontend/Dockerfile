FROM node:26-alpine AS build

WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm ci

ARG VITE_API_URL=https://mern-api.dibyashworhamal.com.np/api
ENV VITE_API_URL=$VITE_API_URL

# Copy the rest of the source code
COPY . .

# Build the Vite project
RUN npm run build

FROM nginx:alpine

# Copy the built files
COPY --from=build /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]