## 🛠️ RAG Microservice Application

This is a microservice-based Retrieval-Augmented Generation (RAG) application built using **Spring Boot** and **FastAPI**, fully containerized with **Docker Compose**.

## ✨ Overview

The project consists of two main services:
- **ApplicationService** (Spring Boot): Handles application-specific logic and database operations.
- **RagService** (FastAPI): Manages RAG-specific functionalities like file ingestion and retrieval.

Both services work together seamlessly through message-driven communication and an API Gateway.

## 🧩 Architecture

- **Spring Boot (ApplicationService)**: Main backend service, connected to an **H2 database**.
- **FastAPI (RagService)**: Handles file uploads, storage, and embedding generation.
- **RabbitMQ**: Message broker for inter-service communication.
- **MinIO**: Acts as the object storage bucket for `.txt` and `.pdf` files.
- **Kong API Gateway**:
  - `/api/**` routes to the **Spring Boot** service.
  - `/rag/**` routes to the **FastAPI** service.
- **Embedding Model**: Uses **All-MiniLM** for generating embeddings.
- **Vector Database**: Uses **Chroma DB** for storing embeddings.
- **LLM API**: Uses Groq API for generating formatted RAG responses from context.
## ⚙️ Environment Variables



## 🗄️ Storage and Messaging

- **MinIO** storage is initialized automatically with a default bucket named `rag`.
- **RabbitMQ** is used for asynchronous communication between the Spring Boot and FastAPI services.

---

## 🌐 API Gateway

**Kong Gateway** routes requests to:
- `/api/**` → ApplicationService (Spring Boot)
- `/rag/**` → RagService (FastAPI)

The Kong configuration is loaded declaratively from the `./kong/` directory.

### FastAPI (RagService)

```env
FILE_UPLOAD_PATH=resource/uploads
KNOWLEDGE_BASE_PATH=resource/knowledge_base
RAG_EMBEDDINGS_MODEL=embeddings_model
CHROMA_DB_URL=chromadb

LLM_API_ENDPOINT=https://api.groq.com/openai/v1/chat/completions
LLM_API_TOKEN=**GROK_API_KEY**
LLM_MODEL_NAME=gemma2-9b-it

STORAGE_ENDPOINT=storage:9000
STORAGE_ACCESS_KEY=java_user
STORAGE_SECRET_KEY=java_user
STORAGE_BUCKET=rag

MESSAGING_HOST=messaging
MESSAGING_PORT=5672
MESSAGING_USERNAME=java_user
MESSAGING_PASSWORD=java_user

MESSAGING_EXCHANGE_NAME=file.processing.exchange
MESSAGING_REQUEST_QUEUE=file.processing.request.queue
MESSAGING_REQUEST_QUEUE_ROUTING_KEY=file.processing.request
MESSAGING_RESPONSE_QUEUE=file.processing.response.queue
MESSAGING_RESPONSE_QUEUE_ROUTING_KEY=file.processing.response

PYTHONDONTWRITEBYTECODE=1

# Server Configuration
API_PREFIX=/
PORT=8080

# Spring Profiles
ACTIVE_PROFILE=prod

# Multipart Settings
MAX_REQUEST_FILE_SIZE=100
MAX_REQUEST_SIZE=100

# Database
DATABASE_JDBC_URL=jdbc:h2:tcp://application-db:9092//db/data/rag
DATABASE_USERNAME=sa
DATABASE_PASSWORD=java_user

# Storage
STORAGE_URL=http://storage:9000
STORAGE_ACCESS_KEY=java_user
STORAGE_SECRET_KEY=java_user
STORAGE_BUCKET=rag
STORAGE_UPLOAD_PATH=uploads

# Messaging
MESSAGING_HOST=messaging
MESSAGING_PORT=5672
MESSAGING_USERNAME=java_user
MESSAGING_PASSWORD=java_user
MESSAGING_EXCHANGE_NAME=file.processing.exchange
MESSAGING_REQUEST_QUEUE=file.processing.request.queue
MESSAGING_REQUEST_QUEUE_ROUTING_KEY=file.processing.request
MESSAGING_RESPONSE_QUEUE=file.processing.response.queue
MESSAGING_RESPONSE_QUEUE_ROUTING_KEY=file.processing.response

# Logging
LOGGING_LEVEL=INFO
LOGGING_PATH=logs

## 🐳 Dockerized Setup

All services are dockerized and orchestrated via **Docker Compose**. Spin up the entire stack with:
## 📜 API Endpoints

### FastAPI (RagService)

- **GET** `/rag/collections/{collection_name}/count`  
  Get the document count in a specific collection.

- **POST** `/rag/collections/{collection_name}`  
  Query a specific collection.

### Spring Boot (ApplicationService)

- **GET** `/files`  
  Retrieve all uploaded files.

- **POST** `/files`  
  Upload a list of files (requires `username` as query parameter).

- **POST** `/files/add-to-knowledge-base`  
  Add uploaded files to a knowledge base collection (requires `username` and collection info).

Both services expose their OpenAPI documentation:
- FastAPI Swagger UI: `http://localhost:8081/docs`
- Spring Boot Swagger UI: `http://localhost:8080/docs`

---

## 🐳 Docker Compose Services

This project uses **Docker Compose** to spin up all necessary services:

| Service            | Description                                    | Ports                         |
|--------------------|------------------------------------------------|-------------------------------|
| **application-db**  | H2 Database running in TCP server mode        | 8082, 9092                    |
| **storage**         | MinIO Object Storage                          | 9000 (API), 9001 (Console)     |
| **bucket-creator**  | Initializes the required MinIO buckets        | Internal only                 |
| **messaging**       | RabbitMQ message broker (management enabled)  | 5672, 15672                   |
| **rag**             | FastAPI RAG microservice                      | 8081 → 8000                   |
| **application**     | Spring Boot ApplicationService               | 8080                          |
| **gateway**         | Kong API Gateway                              | 8000, 8443, 8001, 8444, etc.   |

![Spring Boot Docs](https://github.com/user-attachments/assets/98a95589-6a6d-4879-8365-1ddfcfd4f648)

![Fast API docs](https://github.com/user-attachments/assets/6fa8bc42-7c72-4613-b634-109e71ccd0fb)


Services are connected via the `application-network`.



```bash
docker-compose up
