 # Spring Boot: How to design an efficient REST API?
 [![Build Status](https://travis-ci.org/Raouf25/Spring-Boot-efficient-search-API.svg?branch=master)](https://travis-ci.org/Raouf25/Spring-Boot-efficient-search-API)
 [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Raouf25_Spring-Boot-efficient-search-API&metric=alert_status)](https://sonarcloud.io/dashboard?id=Raouf25_Spring-Boot-efficient-search-API)
 [![BCH compliance](https://bettercodehub.com/edge/badge/Raouf25/Spring-Boot-efficient-search-API?branch=master)](https://bettercodehub.com/)

## Objective
Take the previous version of efficient search API and enable it for **OKTA OAuth2.0 Machine2Machine** instead of Web based OAuth2.0. The m2m method implements **Client Credentials flow** and is recommended for server-side (AKA confidential) client applications with no end user, which normally describes machine-to-machine communication. **Example:** One API calling another API which is secured via OKTA. Your client application/API needs to securely store its Client ID and secret and pass those to Okta in exchange for an access token. At a high-level, the flow only has two steps:

### Changes done from the original fork (https://github.com/Raouf25/Spring-Boot-efficient-search-API)
* docker-compose added
* Dockerfile changed to skip tests
* mysql-dump, data and config directories added to support MYSQL database initialization
* pom.xml changed to include mysql dependency
* pom.xml changed
  * **Removed** OKTA security dependency for Web based OAuth2
  * Spring security dependencies added
```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.security.oauth.boot</groupId>
            <artifactId>spring-security-oauth2-autoconfigure</artifactId>
            <version>2.0.0.RELEASE</version>
        </dependency>
```
* application.yml changed to externalize the DB connection from H2 to MYSQL
* **application.yml changed to accomodate OKTA m2m methods include token instrospection**	
* Main application class (SpringBootEfficientSearchApiApplication.java) changed to import spring security classes and enable application as a resource server
```
package com.mak.springbootefficientsearchapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

@EnableResourceServer
@SpringBootApplication
public class SpringBootEfficientSearchApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootEfficientSearchApiApplication.class, args);
    }
}
```
* data.sql changed to a full dump and moved to mysql-dump directory instead of /src/main/resources
* To run simply execute "docker-compose up -d --build" (change variables as needed in docker-compose.yml)

### STEP-1 OKTA SETUP 
* Register via https://developer.okta.com/login/ and any method (google, github, etc.)
* Complete initial nuances of goals, etc.
* Top left hand menu icon > directory > people. Finish the addition of a user and send activation email. 
* Regardless, the user for which the account has been created needs to activate their account via the email received and set a password for themselves

![image](https://user-images.githubusercontent.com/39495790/121224644-6637ee80-c8a6-11eb-8d4e-a1c00a191466.png)

* Alternatively you can do a group based assignment also., but for now we will stick to single user
* Next again click on menu and Applications > Create App Integration

![image](https://user-images.githubusercontent.com/39495790/121224542-4d2f3d80-c8a6-11eb-9f4f-3abaf79e7215.png)

![image](https://user-images.githubusercontent.com/39495790/121312663-3aa71980-c923-11eb-9244-f4fe445c290a.png)

![image](https://user-images.githubusercontent.com/39495790/121312780-590d1500-c923-11eb-973e-70b718d6720e.png)

* Once saved you will see the app summary. Copy the client id and secret. 

![image](https://user-images.githubusercontent.com/39495790/121313014-9a052980-c923-11eb-8b27-8e39eee76429.png)

* Navigate through left hand menu > security > API. Herein we will need to do a couple of things as next steps
* Click on the default authorization server to retrive the Issuer URL, Token URL and set a custom scope

![image](https://user-images.githubusercontent.com/39495790/121314403-f0bf3300-c924-11eb-943c-224b7f9d2182.png)

* On the following screen
  * Copy the Issuer URL. This will be used in the docker-compose file as an environment variable
  * Then click the MetaData URL. It will open a new page, wherein we need to copy the Token URL
  
![image](https://user-images.githubusercontent.com/39495790/121315255-cb7ef480-c925-11eb-8279-54aa5547ecb1.png)
![image](https://user-images.githubusercontent.com/39495790/121315974-78597180-c926-11eb-859f-1f4a5ad6c4ef.png)

* Once we have the Issuer and Token URLs handy. On the default authorization server page, click on scopes to create a custom scope

![image](https://user-images.githubusercontent.com/39495790/121316427-f158c900-c926-11eb-82d3-500e13865f30.png)

* Add a scope and ensure that you have the name defined here handy as it will be required for any client API to generate tokens

![image](https://user-images.githubusercontent.com/39495790/121316583-19482c80-c927-11eb-81da-31bd5bdf7503.png)

* Once done, you will be able to see the created scope

![image](https://user-images.githubusercontent.com/39495790/121316714-3aa91880-c927-11eb-8317-e0ae8785629e.png)

> As a summary, you would have created a M2M app integration and retrieved the **Issuer URL, Token URL, Client ID, Client Secret, Scope name**. These variables are required for next steps

### STEP-2 MODIFY DOCKER-COMPOSE
* Edit the docker-compose.yml in the parent directory

> NOTE: Don't do a copy paste error between Issuer URL and Token URL

* Insert the values for **Issuer URL, Client ID and Client Secret** in between the empty quotes against the respective variables set up for springboot app
* Adjust the db and related env variables as per your choice

### RUN THE APPLICATION
* docker-compose up -d --build
* Visit the url http://ip-address-or-dns-hostname:8031/api/cars


### NOTE on Actual usage of the API

Resource collections are often enormous, and when some data has to be retrieved from them, it would be only not very efficient to always get the full list and browse it for specific items. Therefore we should design an optimized Search API.

A few of the essential features for consuming an API are:
- **Filtering:** 
to narrow down the query results by specific parameters, eg. creation date, or country
```
GET /api/cars?country=USA
GET /api/cars?createDate=2019–11–11
```

- **Sorting:** 
basically allows sorting the results ascending or descending by a chosen parameter or parameters, eg. by date
```
GET /api/cars?sort=createDate,asc
GET /api/cars?sort=createDate,desc
```

- **Paging:**  
uses “size” to narrow down the number of results shown to a specific number and “offset” to specify which part of the results range to be shown 
— this is important in cases where the number of total results is greater than the one presented, this works like pagination you may encounter on many websites
Usually, these features are used by adding a so-called query parameter to the endpoint that is being called. 
```
GET /api/cars?limit=100
GET /api/cars?offset=2
```

All together:
```
GET /api/cars?country=USA&sort=createDate,desc&limit=100&offset=2
```
This query should result in the list of 100 cars from the USA, sorted descending by the creation date, and the presented records are on the second page, which means they are from a 101–200 record number range.

##### Test application

```
$  curl localhost:8080/api/cars/1
```

the response should be:
```json
{
   "id":1,
   "manufacturer":"Acura",
   "model":"Integra",
   "type":"Small",
   "country":"Japon",
   "createDate":"1931-02-01"
}
```

#####  Stop Docker Container:
```
docker stop `docker container ls | grep "spring-boot-efficient-search-api:*" | awk '{ print $1 }'`
```

## Live Demo
This project is deployed in https://efficient-search-api.herokuapp.com/api/cars

Let's try: https://efficient-search-api.herokuapp.com/api/cars?country=USA&sort=createDate,desc&limit=100&offset=2

## License
For more details please see this **[medium post](https://medium.com/quick-code/spring-boot-how-to-design-efficient-search-rest-api-c3a678b693a0)** .

Spring Boot Efficient Search Api Copyright © 2020 by Abderraouf Makhlouf <makhlouf.raouf@gmail.com>
