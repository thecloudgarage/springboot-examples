# Objective
Spring boot and Postgresql API that creates, reads, updates values by name and not by typical id

## Prerequisites
Docker and Docker compose installed

## Steps to recreate
* Clone the repo and create an empty directory named data to hold postgresql data
* In case db name/user/password needs a change., edit the variables in docker-compose.yml

```
docker-compose up -d --build
```

## INITIAL GET (prepopulated database with 4 entries)

```
curl http://ip-or-hostname:8031/api/v1/gpsinventory
```

Results

```
[{
    "id": 1,
    "gpsterminalId": "gps-01-001",
    "customerId": "1",
    "customerName": "ABC001 limited",
    "lat": 0.0,
    "lon": 0.0
}, {
    "id": 2,
    "gpsterminalId": "gps-01-002",
    "customerId": "2",
    "customerName": "ABC002 limited",
    "lat": 0.0,
    "lon": 0.0
}, {
    "id": 3,
    "gpsterminalId": "gps-01-003",
    "customerId": "3",
    "customerName": "ABC003 limited",
    "lat": 0.0,
    "lon": 0.0
}, {
    "id": 4,
    "gpsterminalId": "gps-01-004",
    "customerId": "4",
    "customerName": "ABC004 limited",
    "lat": 0.0,
    "lon": 0.0
}]
```

## POST a new value
```
curl -H "Content-Type: application/json" -X POST -d '{"gpsterminalId": "gps-01-005", "customerId": 1, "customerName": "ABC001 limited"}' http://ip-or-hostname:8031/api/v1/gpsinventory
```

> YOU MIGHT GET AN ERROR OF UNABLE TO EXECUTE SQL DUE TO DUPLICATE KEY
> **DONT FRET** Just repeat the command and from thereon all is well! (not sure why postgresql issues a duplicate key error upon first entry after getting ininitialization. Not bothering to troubleshoot as a re-run of the command solves it. It only happens for the first insert

Response
```
{
    "id": 5,
    "gpsterminalId": "gps-01-005",
    "customerId": "1",
    "customerName": "ABC001 limited",
    "lat": 0.0,
    "lon": 0.0
}
```

## GET all entries
```
curl http://ip-or-hostname:8031/api/v1/gpsinventory
```

Response
```
[{
    "id": 1,
    "gpsterminalId": "gps-01-001",
    "customerId": "1",
    "customerName": "ABC001 limited",
    "lat": 0.0,
    "lon": 0.0
}, {
    "id": 2,
    "gpsterminalId": "gps-01-002",
    "customerId": "2",
    "customerName": "ABC002 limited",
    "lat": 0.0,
    "lon": 0.0
}, {
    "id": 3,
    "gpsterminalId": "gps-01-003",
    "customerId": "3",
    "customerName": "ABC003 limited",
    "lat": 0.0,
    "lon": 0.0
}, {
    "id": 4,
    "gpsterminalId": "gps-01-004",
    "customerId": "4",
    "customerName": "ABC004 limited",
    "lat": 0.0,
    "lon": 0.0
}, {
    "id": 5,
    "gpsterminalId": "gps-01-005",
    "customerId": "1",
    "customerName": "ABC001 limited",
    "lat": 0.0,
    "lon": 0.0
}]
```

or query  the specific gps terminal id

```
curl http://ip-or-hostname:8031/api/v1/gpsinventory/gps-01-001
```

Response
```
{
    "id": 5,
    "gpsterminalId": "gps-01-005",
    "customerId": "1",
    "customerName": "ABC001 limited",
    "lat": 0.0,
    "lon": 0.0
}
```

## UPDATE/PUT the latitude value 
> **NOTE** the gps id for which it is updated is at the end of the url. updates happen not by typical Id but by name of the Gps terminal)

```
curl -H "Content-Type: application/json" -X PUT -d '{"gpsterminalId": "gps-01-001", "customerId": 1, "customerName": "ABC001 limited", "lat": 12.9780, "lon": 77.5946}' http://ip-or-hostname:8031/api/v1/gpsinventory/gps-01-001
```

OBSERVE the change in latitude value for the specific gps termainal

```
curl http://ip-or-hostname:8031/api/v1/gpsinventory/gps-01-001
```

Response
```
{
    "id": 1,
    "gpsterminalId": "gps-01-001",
    "customerId": "1",
    "customerName": "ABC001 limited",
    "lat": 12.978,
    "lon": 77.5946
}
```

### Projects observed for reference:
* https://www.javaguides.net/2019/01/springboot-postgresql-jpa-hibernate-crud-restful-api-tutorial.html
* https://bushansirgur.in/spring-data-jpa-finder-methods-by-field-name-with-examples/
* https://github.com/TechPrimers/spring-data-rest-jpa-example/blob/master/src/main/java/com/techprimers/controller/UsersController.java
