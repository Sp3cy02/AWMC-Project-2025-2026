# AWMC-Project-2025-2026
Progetto di Applicazioni Web Mobile Cloud UNICAM 2025/2026

Istruzioni per avviare i servizi

Posizionarsi nella cartella principale del backend dove si trova docker-compose.yml:

cd hackhub

Avviare MySQL:

docker compose up -d

Verificare che il container sia attivo:

docker ps

Restare nella cartella backend (hackhub).

Se è la prima volta:

.\mvnw.cmd clean install

Poi avviare:

.\mvnw.cmd spring-boot:run

Entrare nella cartella frontend:

cd frontend/hackhub-frontend

Installare le dipendenze (solo la prima volta):

npm install

Avviare Angular:

npm start

Oppure:

ng serve
