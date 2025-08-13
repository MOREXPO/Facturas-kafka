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

<img width="1862" height="963" alt="image" src="https://github.com/user-attachments/assets/a8d67285-2731-41f6-8adb-8d08b842a32d" />

<img width="1885" height="930" alt="image" src="https://github.com/user-attachments/assets/352867e9-bc13-4ef0-bd3c-0c1d91847395" />

### Kafka Message Publishing

<img width="1241" height="839" alt="image" src="https://github.com/user-attachments/assets/4dbcf9e9-8f4c-4432-b3de-a492aef83665" />

<img width="1165" height="782" alt="image" src="https://github.com/user-attachments/assets/12a5ae56-5ed4-421c-b99c-3dcf94673564" />

### Validated Invoice in XML

<img width="972" height="144" alt="image" src="https://github.com/user-attachments/assets/cf773045-fcb6-4d5f-a75f-84ac6875ae96" />

<img width="373" height="127" alt="image" src="https://github.com/user-attachments/assets/30da2d06-3a87-4e18-9e5a-44911c098097" />

### Invalid Invoice Storage in H2

<img width="1274" height="819" alt="image" src="https://github.com/user-attachments/assets/c107fcd4-7d1c-4af2-9cbb-611737f3f081" />

<img width="1507" height="975" alt="image" src="https://github.com/user-attachments/assets/eb0bf95e-8982-455a-909c-e22e972d79e8" />

