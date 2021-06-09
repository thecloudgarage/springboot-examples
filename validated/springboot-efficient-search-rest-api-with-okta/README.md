 # Spring Boot: How to design an efficient REST API?
 [![Build Status](https://travis-ci.org/Raouf25/Spring-Boot-efficient-search-API.svg?branch=master)](https://travis-ci.org/Raouf25/Spring-Boot-efficient-search-API)
 [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Raouf25_Spring-Boot-efficient-search-API&metric=alert_status)](https://sonarcloud.io/dashboard?id=Raouf25_Spring-Boot-efficient-search-API)
 [![BCH compliance](https://bettercodehub.com/edge/badge/Raouf25/Spring-Boot-efficient-search-API?branch=master)](https://bettercodehub.com/)

## Objective
Take the previous version of efficient search API and enable it for **OKTA OAuth2.0 Web App authentication**. When you browse the URL, it will redirect the page to OKTA authentication page and upon success you will be redirected to the API output page. Implements OpenId connect Web Application method of OKTA for OAuth2.0

### Changes done from the original fork (https://github.com/Raouf25/Spring-Boot-efficient-search-API)
* docker-compose added
* Dockerfile changed to skip tests
* mysql-dump, data and config directories added to support MYSQL database initialization
* pom.xml changed to include mysql dependency
* **pom.xml changed to include OKTA dependency**
* **application.properties** changed to **application.yml** (used http://www.allencoders.online/converters/props2yaml)
* application.yml changed to externalize the DB connection from H2 to MYSQL
* **application.yml changed to externalize OKTA configuration**	
* data.sql changed to a full dump and moved to mysql-dump directory instead of /src/main/resources
* To run simply execute "docker-compose up -d --build" (change variables as needed in docker-compose.yml)

> **NOTE** pom.xml has one okta dependency included and that's all! Everything else is managed by OKTA configurations.
> 
### STEP-1 OKTA SETUP 
* Register via https://developer.okta.com/login/ and any method (google, github, etc.)
* Complete initial nuances of goals, etc.
* Top left hand menu icon > directory > people. Finish the addition of a user and send activation email. 
* Regardless, the user for which the account has been created needs to activate their account via the email received and set a password for themselves

![image](https://user-images.githubusercontent.com/39495790/121224644-6637ee80-c8a6-11eb-8d4e-a1c00a191466.png)

* Alternatively you can do a group based assignment also., but for now we will stick to single user
* Next again click on menu and Applications > Create App Integration

![image](https://user-images.githubusercontent.com/39495790/121224542-4d2f3d80-c8a6-11eb-9f4f-3abaf79e7215.png)

![image](https://user-images.githubusercontent.com/39495790/121225400-1c9bd380-c8a7-11eb-8ca8-e93c38b3e9ec.png)

![image](https://user-images.githubusercontent.com/39495790/121225827-8b792c80-c8a7-11eb-8a21-66a99825cae3.png)

* Select the grant types as shown below and the value of Login redirect URI as shown. 
* Example http://3.142.146.96:8031/login/oauth2/code/okta (ensure your ip/dnsname:port on which springboot app will run is followed by the specified string)

![image](https://user-images.githubusercontent.com/39495790/121231479-165d2580-c8ae-11eb-8828-cae31dfb721b.png)

* For now, leave everything else to default values and save
* **Optionally** you can secure the authentication request such that it will be accepted only from your IP/DNS name by setting the base URI. You can type in http://your-ip-or-hostname:portnumber (this is validated., so no problems)

![image](https://user-images.githubusercontent.com/39495790/121231960-aac78800-c8ae-11eb-8f58-8592c4e6130b.png)

* Once saved you will see the app summary. Copy the client id and secret. We have to edit the app again to set the **Initiate Login URI**

![image](https://user-images.githubusercontent.com/39495790/121228865-e6f8e980-c8aa-11eb-8f81-64efd83dfa82.png)

* Scroll down and insert the **Initiate Login URI** and save

![image](https://user-images.githubusercontent.com/39495790/121229761-004e6580-c8ac-11eb-9407-b874b26ff550.png)

* You will be headed back to the App summary page. As of now all users in your organization are permitted to use this app. One can edit the assignments and delete the users. Optionally, if one has setup group based assignments, the authorization can be restricted at group level

![image](https://user-images.githubusercontent.com/39495790/121232805-9768ec80-c8af-11eb-885a-30dab1ebf020.png)

* If not yet click on General tab and copy the Client ID and Client Secret ID. Store these for later use
* Click on left hand menu > Security > API. Here you will see the default authorization server for your account. Copy the Issuer URL and store this for later use

![image](https://user-images.githubusercontent.com/39495790/121233644-73f27180-c8b0-11eb-880a-eecaeb0dfc78.png)


### STEP-2 MODIFY DOCKER-COMPOSE
* Edit the docker-compose.yml in the parent directory
* Insert the values for **Issuer URL, Client ID and Client Secret** in between the empty quotes against the respective variables set up for springboot app
* Adjust the db and related env variables as per your choice

### RUN THE APPLICATION
```
docker-compose up -d --build
```
* Visit the url http://ip-address-or-dns-hostname:8031/api/cars
* Perform OAuth2 authentication with OKTA and validate redirect to the page. The user ID used to authenticate should be in the assignments list of Okta App and the user should have activated their accounts via the email received

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

## License (original credits to below)
For more details please see this **[medium post](https://medium.com/quick-code/spring-boot-how-to-design-efficient-search-rest-api-c3a678b693a0)** .

Spring Boot Efficient Search Api Copyright © 2020 by Abderraouf Makhlouf <makhlouf.raouf@gmail.com>
