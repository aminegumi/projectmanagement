# Build stage for Frontend
FROM node:16
WORKDIR /projectmanagement/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
# Supprimer cette ligne qui cause l'erreur
# ENV NODE_OPTIONS=--openssl-legacy-provider
RUN npm run build

# Exposer le port 3000 pour le serveur de développement
EXPOSE 3000
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]