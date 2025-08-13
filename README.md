# Invoices Microservice

A Spring Boot microservice that processes electronic invoices from Kafka, validates them according to business rules, stores invalid invoices in an H2 database, and transforms valid invoices into XML format.  
It uses Kafka Streams for processing and integrates with Swagger/OpenAPI for documentation.

---

## 🛠 Technology Stack

- **Java 24**
- **Spring Boot 3**
  - Spring Kafka (Kafka Streams)
  - Spring Data JPA (H2 database)
  - Spring Web (REST API)
- **Apache Kafka**
- **H2 Database**
- **Swagger / OpenAPI 3** for API documentation
- **JUnit 5** + **EmbeddedKafka** for integration tests
- **Docker Compose** for local development

---

## 🚀 Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/MOREXPO/Facturas-kafka.git
cd Facturas-kafka
```

### 2. Start Kafka & H2 with Docker Compose

```bash
docker-compose up -d
```

This will start:

Kafka broker (localhost:9092)

H2 Database (web console at http://localhost:81)

### 3. Run the application

```bash
./mvnw spring-boot:run
```

By default:

- Kafka topic for input: incoming-invoices

- Kafka topic for output: validated-invoices


### 4. Access the API documentation

```bash
http://localhost:8080/swagger-ui/index.html
```

## 📦 Sample Kafka Message
### You can publish a message to the incoming-invoices topic for testing:
```json
{
  "invoiceId": "INV-20240101-001",
  "customerId": "12345678A",
  "country": "DE",
  "issueDate": "2024-01-01",
  "lines": [
    { "description": "Laptop", "amount": 200, "vatRate": 0.19 }
  ]
}
```

Example using Kafka CLI:
```bash
docker exec -it broker kafka-console-producer \
  --broker-list localhost:9092 \
  --topic incoming-invoices
```
Paste the JSON above and press Enter.

## 📡 REST API Example
### POST /api/invoices
Request body:
```json
{
  "invoiceId": "INV-20240101-001",
  "customerId": "12345678A",
  "country": "DE",
  "issueDate": "2024-01-01",
  "lines": [
    { "description": "Laptop", "amount": 200, "vatRate": 0.19 }
  ]
}
```
Success response (200):
```json
{
  "status": "OK",
  "xml": "<Invoice>...</Invoice>"
}
```
Error response (400):
```json
{
  "status": "ERROR",
  "errorMessage": "Invoice is invalid"
}
```

## 🧪 Testing

```bash
Run unit and integration tests:
```

Integration tests use:

- EmbeddedKafka to simulate Kafka topics

- H2 in-memory database for persistence testing

## 📌 Technical Decisions

### Kafka Streams for Processing
- Chosen to provide real-time stream processing directly from Kafka topics.

- Ensures scalability and exactly-once processing when needed.

### H2 in Docker (instead of only in-memory)
- Decided to run H2 Database in a Docker container to allow:

    - Easy inspection of stored invalid invoices via web console.

    - Persistence across application restarts in development.

    - Integration with other services without embedding the DB inside the app.

- Combined with Kafka in the same docker-compose.yml for simple local environment setup.

## Practical Examples

### Swagger UI

<img width="1897" height="970" alt="image" src="https://github.com/user-attachments/assets/0c56159f-9194-4246-9070-72c5e52a2b23" />

### Kafka Message Publishing

<img width="1176" height="772" alt="image" src="https://github.com/user-attachments/assets/898b28ab-a35b-47fa-9786-8f03315b10ed" />

### Validated Invoice in XML

<img width="977" height="197" alt="image" src="https://github.com/user-attachments/assets/e66c3a9d-7041-4bcf-a015-54b2f9fcf57e" />

### Invalid Invoice Storage in H2

<img width="1061" height="808" alt="image" src="https://github.com/user-attachments/assets/3cd6c86b-5ee8-47f2-8730-8f230dd959a1" />

<img width="1803" height="975" alt="image" src="https://github.com/user-attachments/assets/2bde2d0f-bc32-4cad-9d7a-d46095b57da8" />

