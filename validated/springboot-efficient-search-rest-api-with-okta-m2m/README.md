# Spring Boot, Boomi & OKTA: How to design an efficient REST API with OAuth2.0 Machine to Machine workflow?

## Objective
Take the previous version of efficient search API and enable it for **OKTA OAuth2.0 Machine2Machine** instead of Web based OAuth2.0. The m2m method implements **Client Credentials flow** and is recommended for server-side (AKA confidential) client applications with no end user, which normally describes machine-to-machine communication.

**Example:** One API calling another API which is secured via OKTA. Your client application/API needs to securely store its Client ID and secret and pass those to Okta in exchange for an access token. At a high-level, the flow only has two steps:

![image](https://user-images.githubusercontent.com/39495790/121356158-3cd29d80-c94e-11eb-9ea4-adb639f7a40b.png)

### Highlights
* Original repo for the springboot basic app (https://github.com/Raouf25/Spring-Boot-efficient-search-API)
* **Changes done**
  * The Springboot application is dockerized and supplied with docker-compose for ease of deployments
  * **Boomi HTTP client process** to emulate Client Credentials Grant workflow
  * **CURL client** to manually emulate the Client Credentials Grant workflow
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
  
  * **application.properties** changed to **application.yml** (used http://www.allencoders.online/converters/props2yaml)
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
  
### How to run

* Complete OKTA setup as shown in the below diagrams
* Once done, simply execute "docker-compose up -d --build" (change variables as needed in docker-compose.yml)
* Execute Curl tests to verify API & OAuth2 M2M functionality
* Build the client app on Boomi and erify API & OAuth2 M2M functionality

### OKTA SETUP 
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
```
docker-compose up -d --build
```
### Consuming the Springboot API

* Retrieve a token using the token endpoint, client id, client secret and custom scope name defined above

```
curl -v -X POST \
-H "Content-type:application/x-www-form-urlencoded" \
"<your-token-url>" \
-d "client_id=<your-client-id>&client_secret=<your-client-secret>&grant_type=client_credentials&scope=<your-scope-name>"
 ```
 
* You should see an ouput similar to:

```
{
    "token_type": "Bearer",
    "expires_in": 3600,
    "access_token": "LONG-STRING-OF-TOKEN",
    "scope": "springboot-secure-rest-api"
}
```
* Copy the long string (without quotes) of the token
* All this effort for WHAT :) Issue the command 

```
curl -H "Authorization: Bearer <paste-your-token-without-quotes>" http://ip-address-or-dns-name:8031/api/cars
```
If you followed the steps correctly, we will receive our expected JSON response from the secured Springboot API. Example:

```
[{
    "id": 1,
    "manufacturer": "Acura",
    "model": "Integra",
    "type": "Small",
    "country": "Japon",
    "createDate": "1931-02-01"
}, {
    "id": 2,
    "manufacturer": "Acura",
    "model": "Legend",
    "type": "Midsize",
    "country": "Japon",
    "createDate": "1959-07-30"
}, {
    "id": 3,
    "manufacturer": "Audi",
    "model": "90",
    "type": "Compact",
    "country": "Germany",
    "createDate": "1970-07-30"
}, {
    "id": 4,
    "manufacturer": "Audi",
    "model": "100",
    "type": "Midsize",
    "country": "Germany",
    "createDate": "1963-10-04"
}, {
    "id": 5,
    "manufacturer": "BMW",
    "model": "535i",
    "type": "Midsize",
    "country": "Germany",
    "createDate": "1931-09-08"
}, {
    "id": 6,
    "manufacturer": "Buick",
    "model": "Century",
    "type": "Midsize",
    "country": "USA",
    "createDate": "1957-02-20"
}, {
    "id": 7,
    "manufacturer": "Buick",
    "model": "LeSabre",
    "type": "Large",
    "country": "USA",
    "createDate": "1968-10-23"
}, {
    "id": 8,
    "manufacturer": "Buick",
    "model": "Roadmaster",
    "type": "Large",
    "country": "USA",
    "createDate": "1970-08-17"
}, {
    "id": 9,
    "manufacturer": "Buick",
    "model": "Riviera",
    "type": "Midsize",
    "country": "USA",
    "createDate": "1962-08-02"
}, {
    "id": 10,
    "manufacturer": "Cadillac",
    "model": "DeVille",
    "type": "Large",
    "country": "USA",
    "createDate": "1956-12-01"
}, {
    "id": 11,
    "manufacturer": "Cadillac",
    "model": "Seville",
    "type": "Midsize",
    "country": "USA",
    "createDate": "1957-07-30"
}, {
    "id": 12,
    "manufacturer": "Chevrolet",
    "model": "Cavalier",
    "type": "Compact",
    "country": "USA",
    "createDate": "1952-06-18"
}, {
    "id": 13,
    "manufacturer": "Chevrolet",
    "model": "Corsica",
    "type": "Compact",
    "country": "USA",
    "createDate": "1947-06-26"
}, {
    "id": 14,
    "manufacturer": "Chevrolet",
    "model": "Camaro",
    "type": "Sporty",
    "country": "USA",
    "createDate": "1940-05-27"
}, {
    "id": 15,
    "manufacturer": "Chevrolet",
    "model": "Lumina",
    "type": "Midsize",
    "country": "USA",
    "createDate": "1949-02-21"
}, {
    "id": 16,
    "manufacturer": "Chevrolet",
    "model": "Lumina_APV",
    "type": "Van",
    "country": "USA",
    "createDate": "1944-11-02"
}, {
    "id": 17,
    "manufacturer": "Chevrolet",
    "model": "Astro",
    "type": "Van",
    "country": "USA",
    "createDate": "1962-06-07"
}, {
    "id": 18,
    "manufacturer": "Chevrolet",
    "model": "Caprice",
    "type": "Large",
    "country": "USA",
    "createDate": "1951-01-11"
}, {
    "id": 19,
    "manufacturer": "Chevrolet",
    "model": "Corvette",
    "type": "Sporty",
    "country": "USA",
    "createDate": "1966-11-01"
}, {
    "id": 20,
    "manufacturer": "Chrysler",
    "model": "Concorde",
    "type": "Large",
    "country": "USA",
    "createDate": "1964-07-10"
}, {
    "id": 21,
    "manufacturer": "Chrysler",
    "model": "LeBaron",
    "type": "Compact",
    "country": "USA",
    "createDate": "1938-05-06"
}, {
    "id": 22,
    "manufacturer": "Chrysler",
    "model": "Imperial",
    "type": "Large",
    "country": "USA",
    "createDate": "1960-07-07"
}, {
    "id": 23,
    "manufacturer": "Dodge",
    "model": "Colt",
    "type": "Small",
    "country": "USA",
    "createDate": "1943-06-02"
}, {
    "id": 24,
    "manufacturer": "Dodge",
    "model": "Shadow",
    "type": "Small",
    "country": "USA",
    "createDate": "1934-02-27"
}, {
    "id": 25,
    "manufacturer": "Dodge",
    "model": "Spirit",
    "type": "Compact",
    "country": "USA",
    "createDate": "1932-02-26"
}, {
    "id": 26,
    "manufacturer": "Dodge",
    "model": "Caravan",
    "type": "Van",
    "country": "USA",
    "createDate": "1946-06-12"
}, {
    "id": 27,
    "manufacturer": "Dodge",
    "model": "Dynasty",
    "type": "Midsize",
    "country": "USA",
    "createDate": "1928-03-02"
}, {
    "id": 28,
    "manufacturer": "Dodge",
    "model": "Stealth",
    "type": "Sporty",
    "country": "USA",
    "createDate": "1966-05-20"
}, {
    "id": 29,
    "manufacturer": "Eagle",
    "model": "Summit",
    "type": "Small",
    "country": "USA",
    "createDate": "1941-05-12"
}, {
    "id": 30,
    "manufacturer": "Eagle",
    "model": "Vision",
    "type": "Large",
    "country": "USA",
    "createDate": "1963-09-17"
}, {
    "id": 31,
    "manufacturer": "Ford",
    "model": "Festiva",
    "type": "Small",
    "country": "USA",
    "createDate": "1964-10-22"
}, {
    "id": 32,
    "manufacturer": "Ford",
    "model": "Escort",
    "type": "Small",
    "country": "USA",
    "createDate": "1930-12-02"
}, {
    "id": 33,
    "manufacturer": "Ford",
    "model": "Tempo",
    "type": "Compact",
    "country": "USA",
    "createDate": "1950-04-19"
}, {
    "id": 34,
    "manufacturer": "Ford",
    "model": "Mustang",
    "type": "Sporty",
    "country": "USA",
    "createDate": "1940-06-18"
}, {
    "id": 35,
    "manufacturer": "Ford",
    "model": "Probe",
    "type": "Sporty",
    "country": "USA",
    "createDate": "1941-05-24"
}, {
    "id": 36,
    "manufacturer": "Ford",
    "model": "Aerostar",
    "type": "Van",
    "country": "USA",
    "createDate": "1935-01-27"
}, {
    "id": 37,
    "manufacturer": "Ford",
    "model": "Taurus",
    "type": "Midsize",
    "country": "USA",
    "createDate": "1947-10-08"
}, {
    "id": 38,
    "manufacturer": "Ford",
    "model": "Crown_Victoria",
    "type": "Large",
    "country": "USA",
    "createDate": "1962-02-28"
}, {
    "id": 39,
    "manufacturer": "Geo",
    "model": "Metro",
    "type": "Small",
    "country": "USA",
    "createDate": "1965-10-30"
}, {
    "id": 40,
    "manufacturer": "Geo",
    "model": "Storm",
    "type": "Sporty",
    "country": "USA",
    "createDate": "1955-07-07"
}, {
    "id": 41,
    "manufacturer": "Honda",
    "model": "Prelude",
    "type": "Sporty",
    "country": "Japon",
    "createDate": "1955-06-08"
}, {
    "id": 42,
    "manufacturer": "Honda",
    "model": "Civic",
    "type": "Small",
    "country": "Japon",
    "createDate": "1967-09-16"
}, {
    "id": 43,
    "manufacturer": "Honda",
    "model": "Accord",
    "type": "Compact",
    "country": "Japon",
    "createDate": "1938-06-26"
}, {
    "id": 44,
    "manufacturer": "Hyundai",
    "model": "Excel",
    "type": "Small",
    "country": "South Korea",
    "createDate": "1940-02-25"
}]
```


### NOTE on Actual usage of the API (From hereon don't forget to include the Authorization bearer token in the curl requests)

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

## Credit to the author of original repo for the basic api functionality)
For more details please see this **[medium post](https://medium.com/quick-code/spring-boot-how-to-design-efficient-search-rest-api-c3a678b693a0)** .

License for the basic app: Spring Boot Efficient Search Api Copyright © 2020 by Abderraouf Makhlouf <makhlouf.raouf@gmail.com>
