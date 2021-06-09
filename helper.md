### Convert props to YAML
http://www.allencoders.online/converters/props2yaml

### A quick note on mysql ports
When specifying DB hostname in docker-compose for the springboot API, we can use the service name or the IP address with these guidelines
* 3306:3306 (API can use service name with internal port 3306 or IP address with external port 3306)
* 31003:3306 (API will need to use service name with internal port 3306 or IP address with external port 31003)
* 31003:31003 (API will need to use service with internal port 31003 or IP address with external port 31003. Note the mysql config file needs to be updated with 31003 as port)

**What can go wrong**
* API specifies service name as the DB hostname and DB port as external port number
* Example: service name for mysql is **mysqldb** with 31003:3306 as port assignments. You cannot mention DB hostname for the API as mysqldb with DB port as 31003. It's an invalid configuration and the API will fail to start



