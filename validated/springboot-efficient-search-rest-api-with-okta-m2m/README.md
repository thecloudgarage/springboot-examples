# Spring Boot, Boomi & OKTA: How to design an efficient REST API with OAuth2.0 Machine to Machine workflow?

## Objective
An efficient search API based on Spring boot which is protected via OAuth2.0. Take the original version of efficient search API and enable it for **OKTA OAuth2.0 Machine2Machine** instead of Web based OAuth2.0. The m2m method implements **Client Credentials flow** and is recommended for server-side (AKA confidential) client applications with no end user, which normally describes machine-to-machine communication.

**Additionally** build a **Boomi** based HTTP client that gets authenticated data from the Spring boot API **(OPTIONAL)**

**Example:** An internal API (Boomi HTTP client or CURL) calling another third party API (Spring boot) which is secured via OKTA. Your client application/API needs to securely store its Client ID and secret and pass those to Okta in exchange for an access token. At a high-level, the flow only has two steps:

![image](https://user-images.githubusercontent.com/39495790/121356158-3cd29d80-c94e-11eb-9ea4-adb639f7a40b.png)

### Highlights
* Original repo for the springboot basic app (https://github.com/Raouf25/Spring-Boot-efficient-search-API)
* **CHANGES DONE FOR BUILDING THIS REPO**
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
  
  ```
  security:
  oauth2:
    client:
      clientId: ${oktaClientId}
      clientSecret: ${oktaClientSecret}
    resource:
      tokenInfoUri: ${oktaIssuerUrl}/v1/introspect
  spring:
    datasource:
        password: ${dbpass}
        url: jdbc:mysql://${dbhost}:${dbport}/${dbname}?zeroDateTimeBehavior=convertToNull
        username: ${dbuser}
  ```    
  
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

**SNIP>> START**
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
**SNIP >> ENDS**

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
curl -H "Authorization: Bearer <paste-your-token-without-quotes>" http://ip-address-or-dns-name:8031/api/cars/1
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

## BOOMI process- Emulate the Client APP/API

> **NOTE** The Boomi process of the scenario is optional. You can stop here itself if you are happy working with CURL and/or do not have Boomi account. 
> If you have a Boomi account and want to experiment further., then proceed
> Also note that the docker machine should be reachable from the ATOM via a public or private IP
> If you want to avoid all the hassles, a docker deployment is included in the docker-compose.yml. You can simply replace the token and environment ID for your account and it will spin up a new atom

**Prequisites:** Atom deployed (anywhere, anyhow, Windows latop/server, Linux, Docker/Kubernetes or Boomi Cloud) =and online

The process at the end of the exercise will look this!


![image](https://user-images.githubusercontent.com/39495790/121463339-81eae400-c9cf-11eb-9a2e-919815032dd0.png)

* Begin with a new process component with a No data start shape
* Bring in the HTTP connector and create a new connection & operation to query data from Spring Boot API. Configure the connection to utilize OAuth2.0 authentication via OKTA
* Hook up the HTTP connector to the start shape
* Bring in a disk connection to write queried data to the ATOM's disk volume (Alternatively the disk connector can be replaced with any other target connectors, Databases, etc. supported by Boomi)
* Link the HTTP connector shape with the Disk shape and close it off with a STOP shape

![image](https://user-images.githubusercontent.com/39495790/121465335-43efbf00-c9d3-11eb-8e9d-8b1416bf1d2f.png)

![image](https://user-images.githubusercontent.com/39495790/121465587-b791cc00-c9d3-11eb-84a3-d979d220ed5d.png)

Save and then create a new operation for the connector

![image](https://user-images.githubusercontent.com/39495790/121465821-f32c9600-c9d3-11eb-88fc-75d9b43e07bf.png)

![image](https://user-images.githubusercontent.com/39495790/121466428-0e4bd580-c9d5-11eb-8515-6c9bd6502c4b.png)

![image](https://user-images.githubusercontent.com/39495790/121466780-9c27c080-c9d5-11eb-8691-a6aaa883393b.png)

**Download the profile from the below link and save it on your laptop/machine. Then using the import option use the file to build the JSON profile**

> <a id="raw-url" href="https://github.com/thecloudgarage/springboot-examples/tree/main/validated/springboot-efficient-search-rest-api-with-okta-m2m/boomi_dir/boomi_profiles">CLICK TO DOWNLOAD JSON PROFILE</a>

![image](https://user-images.githubusercontent.com/39495790/121471514-70a8d400-c9dd-11eb-946a-4d2917a0825f.png)

Once the profile is generated you can browse through the elements (these reflect of the data structure that will be received via the api query)

Save every screen and return back to the main process. Link up the http connector and a disk connector.

**THAT'S IT! HIT THE TEST BUTTON AND POST IT'S EXECUTION THE RESULTS CAN BE SEEN IN THE DISK DIRECTORY SPECIFIED FOR THE ATOM**

![image](https://user-images.githubusercontent.com/39495790/121472215-823eab80-c9de-11eb-82ea-dfd797951439.png)

### Credit to the author of original repo for the basic api functionality
For more details please see this **[medium post](https://medium.com/quick-code/spring-boot-how-to-design-efficient-search-rest-api-c3a678b693a0)** .

License for the basic app: Spring Boot Efficient Search Api Copyright © 2020 by Abderraouf Makhlouf <makhlouf.raouf@gmail.com>
