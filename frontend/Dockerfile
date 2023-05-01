# Stage 1: Build Vue application
FROM node:alpine as builder

WORKDIR /app

COPY package*.json ./

# Install dependencies
RUN npm install

# Install vite globally
RUN npm install -g vite

# Copy the whole code to builder and build the app
COPY . .
RUN npm run build

# Stage 2: Create container for running Vue application
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
