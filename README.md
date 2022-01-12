 This is a sample Java 11 / Maven / Spring Boot / Spring Data JPA and Embedded H2 database Restful api for a simple bank transaction application. 
 Where you can register the bank account and make transaction.

Run the app
java -jar booking-service-0.0.1-SNAPSHOT.jar
The integrated Swagger will start running at http://localhost:8888/swagger-ui.html

Database
The embedded H2 database can see at http://localhost:8888/h2-console where:

JDBC URL: jdbc:h2:mem:bankDB
User Name: sa
Password: password
For the app to work successfully it needs to use balances-monitor api, which logs and prints changed daily balances.
