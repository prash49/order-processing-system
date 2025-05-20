Assessment:  building a simplified Order Processing System. When an order is created via an API call, it should:

1.Save the order in a database.
2.Send a message to a queue (order-queue) so that another service can process it asynchronously.
3.Add an endpoint to fetch all orders: GET /orders
4.Add an endpoint to fetch a specific order
 

Requirements:
Implement the following:

1.A Spring Boot REST API endpoint:
   -->POST /orders – accepts an order payload (orderId, item, quantity).
   -->Saves the order in a repository.
   -->Sends the order to an MQ (ActiveMQ/Kafka etc.) 
2.Use an in memory DB
 

Sample Payload:

json
{
  "orderId": 123,
  "item": "iPhone 15",
  "quantity": 2
}

*Tech stack:*
Java
Springboot
Any Database (In memory is also fine)
Packaging manager (Maven/gradle) 

Consideration – Logging, Test cases, Environment support, Health checks
Please make assumptions as per your understanding wherever applicable.

============================================================================================================================================================================
## Tech Stack

*   **Java:** JDK 17+
*   **Spring Boot:** 3.4.5
*   **Dependency Management:** Gradle
*   **Web Framework:** Spring Boot Starter Web (RESTful API)
*   **Persistence:** Spring Boot Starter Data JPA
*   **Database:** MySql
*   **Messaging:** Spring Boot Starter Kafka
*   **JSON Handling:** Jackson (`ObjectMapper`)
*   **Logging:** SLF4j / Logback (Included by Spring Boot starters)
*   **Health Checks:** Spring Boot Starter Actuator
  
## Prerequisites
Before running the application, ensure you have the following installed:

*   Java Development Kit (JDK) 17 or higher
*   Gradle (typically wrapper `./gradlew` handles this, but having it installed globally helps)
*   A running MySql accessible from localhost:3306 ( or update spring.application.datasource.url)
*   A running Kafka Broker accessible from `localhost:9092` (or update `spring.kafka.producer.bootstrap-servers` in `application.yml`)

## Description

This project implements a RESTful API for creating and retrieving orders. When a new order is received via the API, it is persisted in an MySql database, and then details are sent as a JSON message to a configured Kafka topic (`order-created-events`). The application is built using Spring Boot, Spring Data JPA, and Spring Kafka, with Gradle for dependency management.

## Prerequisites

Before running the application, ensure you have the following installed:

*   Java Development Kit (JDK) 17 or higher
*   Gradle (typically wrapper `./gradlew` handles this, but having it installed globally helps)
*   A running Kafka Broker accessible from `localhost:9092` (or update `spring.kafka.producer.bootstrap-servers` in `application.yml`)

## Setup and Running

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd <your-repository-directory>
    ```
2. **Ensure SQL is Running**
3.  **Ensure Kafka is running:**
4.  **Build and Run the application using Gradle:**
    ```bash
    ./gradlew bootRun 
    ```
  
   ### API Documentation

The application exposes the following REST endpoints:

### 1. Create a New Order

*   **Endpoint:** `POST /orders`
*   **Description:** Creates a new order, saves it to the database, and initiates an asynchronous send of the order details to the Kafka topic.
*   **Request Body (JSON):**
    ```json
    {
      "orderId": 123,
      "item": "iPhone 15",
      "quantity": 2
    }
    ```
    *   `orderId`: Unique identifier for the order (used as primary key in H2).
    *   `item`: Name of the item.
    *   `quantity`: Quantity of the item.
*   **Response:**
    *   `201 Created`: If the order is successfully saved and the Kafka send is initiated. Returns the saved Order object.
    *   `409 Conflict`: If an order with the given `orderId` already exists.
    *   `400 Bad Request`: If the request payload is invalid (e.g., missing required fields, though basic validation isn't explicitly implemented here).
    *   `500 Internal Server Error`: If a critical error occurs during processing (e.g., database error, serialization error, immediate Kafka send initiation error).

**Curl Example (POST /orders):**

```bash
curl -X POST \
  http://localhost:8080/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "orderId": 123,
    "item": "iPhone 15",
    "quantity": 2
}'

## 2. Get All Orders
get call:
curl http://localhost:8080/orders

## 3. Get Order by OrderId
get call:
curl http://localhost:8080/orders/123

### 4.Actuator End Points
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/loggers
curl http://localhost:8080/actuator/metrics

