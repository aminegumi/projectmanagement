FROM node:18  

WORKDIR /projectmanagement/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./

# Set environment variable for API URL
ENV VITE_API_URL=http://backend:8082/api
# Add Node options to enable legacy OpenSSL provider
ENV NODE_OPTIONS="--no-experimental-fetch"

EXPOSE 3000
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]