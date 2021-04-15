#!/bin/bash
docker exec -it spring-boot-rest-api-tutorial_mysqldb_1 sh -c 'exec mysql -u root -p123456 <<EOF
USE DB;
create table DB.users(
id int NOT NULL AUTO_INCREMENT primary key,
first_name varchar(50) NOT NULL,
last_name varchar(50) NOT NUll);
EOF'
#PLEASE CHANGE THE USER ID AND PASSWORD
#ALTERNATIVELY THIS CAN ALSO BE DONE VIA A CUSTOMIZED MYSQL IMAGE USING DOCKER ENTRYPOINT FOR INIT DB
