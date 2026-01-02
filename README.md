# Gallery Project (图库项目)

A comprehensive gallery application for DevOps practice, featuring:
- **Backend**: Java SpringBoot + MySQL + Minio
- **Frontend**: Vue 3 + ElementPlus (Cute Style)
- **Mobile**: uni-app
- **DevOps**: Docker, Jenkins, Portainer

## Quick Start (Infrastructure)

1. Ensure Docker and Docker Compose are installed.
2. Run the infrastructure services:
   ```bash
   docker-compose up -d
   ```
3. Services will be available at:
   - MySQL: localhost:3306
   - Minio Console: localhost:9090
   - Minio API: localhost:9000

## Project Structure

- `backend/`: Java SpringBoot application
- `frontend/`: Vue 3 Web application
- `mobile/`: uni-app Mobile application
- `docker-compose.yml`: Infrastructure services definition
