# book-store

This is a book store service back-end application, built with Java, Spring Boot, Docker, MySQL.  
There is implemented JWT bearer authentication. Authorization is provided with RBAC. 
The project follows REST principles implemented with HTTP methods. 

**Functionality:**  
Below is a list of all endpoints in the app. Admin has both roles, so also has access to user endpoints.

Available for unauthorized users:  
- POST: /api/auth/register (Register a new user)
- POST: /api/auth/login (Login an existing user)

Available for USER:  
- GET: /api/books (Get all books)
- GET: /api/books/{id} (Get a specific book)
- GET: /api/categories (Get all categories)
- GET: /api/categories/{id} (Get a specific book)
- GET: /api/categories/{id}/books (Get books by a specific category)
- GET: /api/cart (Get user's shopping cart)
- POST: /api/cart (Add book to the shopping cart)
- PUT: /api/cart/cart-items/{cartItemId} (Update quantity of a book in the shopping cart)
- DELETE: /api/cart/cart-items/{cartItemId} (Delete a book from the shopping cart)
- GET: /api/orders (Get all orders by current user)
- POST: /api/orders (Create an order)
- GET: /api/orders/{orderId}/items (Get all 'order items' in 'user's order')
- GET: /api/orders/{orderId}/items/{itemId} (Get a specific item in 'user's order')

Available for ADMIN:  
- POST: /api/books/ (Create a new book)
- PUT: /api/books/{id} (Update a book)
- DELETE: /api/books/{id} (Delete a book)
- POST: /api/categories (Create a new category)
- PUT: /api/categories/{id} (Update a category)
- DELETE: /api/categories/{id} (Delete a category)
- PATCH: /api/orders/{id} (Update order status)

## Technologies
* Java 17
* Spring Boot 3.1.3, Spring Security, Spring Data JPA
* Docker
* MySQL
* Liquibase
* Swagger

## Installation and Launch
1. Ensure you have Docker installed on your system.
2. Fork this repository.
3. Clone your forked repository.
4. Configure your database settings in the .env file.
5. Build the project with Maven.
6. Build then run it with Docker Compose: 
```
docker-compose build 
docker-compose up
``` 
(If docker can't build your package according to MANIFEST file, execute this `mvn package spring-boot:repackage` before docker commands)  

7. Use Postman or Swagger for sending requests. ([Link for Postman](http://localhost:8081/api/auth/register), [Link for Swagger](http://localhost:8081/api/swagger-ui/index.html))  
for user access -> just register then login  
for admin access -> use or change the credential option in `application.properties` file
