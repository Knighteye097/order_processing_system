# Order Processing System

## Overview

The Order Processing System is a Spring Boot application that handles the creation, retrieval, update, and cancellation of orders. The project uses Java, Maven, and MySQL with Spring Data JPA for persistence.

## Prerequisites

- Java 11 or later  
- Maven  
- MySQL or MariaDB

## Setup

1. **Clone the repository**  
   ```
   git clone https://github.com/Knighteye097/order_processing_system.git
   cd OrderProcessingSystem
   ```

2. **Configure the Database**  
   Update the database properties in the file `src/main/resources/application.properties`:
   - `spring.datasource.url`  
   - `spring.datasource.username`  
   - `spring.datasource.password`

3. **Build the Project**  
   Run the following command to build the project and download dependencies:
   ```
   mvn clean install
   ```

## Running the Application

Run the application using Maven:
```
mvn spring-boot:run
```
The server will start on the port defined in your `application.properties` (default is 8080).

## Endpoints

- **Create Order**  
  `POST /api/orders`  
  Request Body: JSON representing the order details.

- **Get Order by ID**  
  `GET /api/orders/{orderId}`  
  Path variable: order id (numeric).

- **List Orders**  
  `GET /api/orders`  
  Optional RequestParam: order status filter.

- **Update Order Status**  
  `PUT /api/orders/{orderId}`  
  Request Parameter: new order status.

- **Cancel Order**  
  `DELETE /api/orders/{orderId}`

## Testing

Run the tests with:
```
mvn test
```

## License

This project is licensed under the MIT License.
