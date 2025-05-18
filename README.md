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
