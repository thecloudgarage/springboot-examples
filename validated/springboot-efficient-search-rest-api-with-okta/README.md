 # Spring Boot: How to design an efficient REST API?
 [![Build Status](https://travis-ci.org/Raouf25/Spring-Boot-efficient-search-API.svg?branch=master)](https://travis-ci.org/Raouf25/Spring-Boot-efficient-search-API)
 [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Raouf25_Spring-Boot-efficient-search-API&metric=alert_status)](https://sonarcloud.io/dashboard?id=Raouf25_Spring-Boot-efficient-search-API)
 [![BCH compliance](https://bettercodehub.com/edge/badge/Raouf25/Spring-Boot-efficient-search-API?branch=master)](https://bettercodehub.com/)

### Changes done from the original fork (https://github.com/Raouf25/Spring-Boot-efficient-search-API)
* docker-compose added
* Dockerfile changed to skip tests
* mysql-dump, data and config directories added to support MYSQL database initialization
* pom.xml changed to include mysql dependency
* pom.xml changed to include OKTA dependency
* application.properties changed to externalize the DB connection from H2 to MYSQL
* application.properties changed to externalize OKTA configuration	
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

![image](https://user-images.githubusercontent.com/39495790/121225400-1c9bd380-c8a7-11eb-8ca8-e93c38b3e9ec.png)

![image](https://user-images.githubusercontent.com/39495790/121225827-8b792c80-c8a7-11eb-8a21-66a99825cae3.png)

* Select the grant types as shown below and the value of Login redirect URI as shown. Example http://3.142.146.96:8031/login/oauth2/code/okta (ensure your ip/dnsname:port on which springboot app will run is followed by the specified string)

![image](https://user-images.githubusercontent.com/39495790/121227497-42c27300-c8a9-11eb-9762-1034be366594.png)





* 
* Applications > Add application > create new app > platform drop-down > OpenId Connect > Web Application




* Native app (defaults to OpenId Connect) > Create
** Application name: give it a name
** Configure OpenId connect > Login redirect URLs > Add URL > Example http://3.142.146.96:8031/login/oauth2/code/okta (ensure your ip/dnsname:port on which springboot app will run is followed by the specified string)
** Leave logout url as-is & save
** Once saved we need to edit some information starting with Client Credentials (which is shown on the page after save). Click edit and select Use Client Authentication
** Next on the same page click Edit for General Settings and select "Resource Owner Password"
** Unselect Require User Consent
** Now in the initiate login url paste the absolute ip/dns:port on which your springboot app will be running. Example http://3.142.146.96:8031 (no subsequent strings required)
** Scroll up to the app details tabs and click on assignments > Assign > Assign to People > Click on Assign next to the user created earlier > Click Save and Go Back > Done
** Click on General tab and copy the Client ID and Client Secret ID. Store these for later use
** Click on left hand menu > Security > API. Here you will see the default authorization server for your account. Copy the Issuer URL and store this for later use

### STEP-2 MODIFY DOCKER-COMPOSE
* Edit the docker-compose.yml in the parent directory
* Insert the values for Issuer URL, Client ID and Client Secret" in between the empty quotes against the respective variables set up for springboot app
* Adjust the db and related env variables as per your choice

### RUN THE APPLICATION
* docker-compose up -d --build
* Visit the url http://3.142.146.96:8031/api/cars
* Perform oAuth2 authentication with OKTA and validate redirect to the page


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

### How to run the project

##### Clone source code from git
```
$  git clone https://github.com/Raouf25/Spring-Boot-efficient-search-API.git 
```

##### Build Docker image
```
$  docker build -t="spring-boot-efficient-search-api" --force-rm=true .
```
This will first run maven build to create jar package and then build hello-world image using built jar package.

>Note: if you run this command for the first time, it will take some time to download the base image from [DockerHub](https://hub.docker.com/)

##### Run Docker Container
```
$ docker run -p 8080:8080 -it --rm spring-boot-efficient-search-api
```

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
