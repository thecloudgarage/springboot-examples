# ORIGINAL FORK
# Create a REST API in Spring Boot with MYSQL
*The goal of this repository is to create a RESTful API using HTTP methods for CRUD(Create, Retrieve, Update and Delete) operations in Spring Boot along with MYSQL database.*

- Refer [Create a REST API in Spring Boot with MYSQL](https://medium.com/@andriperera.98/create-a-rest-api-in-spring-boot-with-mysql-b250ff3aaa9b) medium article for more details.

# CHANGES DONE
* Added a dockerized version with maven wrapper for springboot api app
* Springboot application.properties is parameterized via variables that can be passed via docker-compose
* mysql database has persistence for data and config
* mysql database is initialized via docker-compose and a DB.sql file is present in mysql-dump directory (change the DB.sql file to suit your needs)

# ADDITIONAL CUSTOMIZATIONS
* The project is now enabled with a different schema
* It contains the following fields for the users table (firstName, lastName, email, city, phone, monthavailable)

# TO RUN
* Install docker & docker-compose
* docker-compose up -d --build

# TO TEST
* curl -X POST -H "Content-Type: application/json" -d '{"firstName": "murugesh", "lastName": "kumar", "city": "bangalore", "email": "murugeshk@gmail.com", "phone": "9901111111", "monthavailable": "january"}' http://<ip-address>:8030/users/
* curl -X http://<ip-address>:8030/users/
* curl -X http://<ip-address>:8030/users/<id>
