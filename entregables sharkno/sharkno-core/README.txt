   _____               
  / ____|              
 | |     ___  _ __ ___ 
 | |    / _ \| '__/ _ \
 | |___| (_) | | |  __/
  \_____\___/|_|  \___|
                       
                                                           
--- Build with Docker ---

docker build . -t core

--- Run with Docker ---

docker run -p 8080:8080 core

--- Run with docker with non-default profile (prod profile example) --

docker run -p 8080:8080 -e PROFILE=prod core

--- Run project with application.properties + profile overrides (dev example)

On your IDE, on java VM options add the following argument:

-Dspring.profiles.active=dev

Eclipse: https://www.cse.wustl.edu/~cosgroved/courses/cse231/f16/javaagent/
IntelliJ: https://stackoverflow.com/questions/50938383/how-to-set-jvm-arguments-in-intellij-idea

This module is prepared to be integrated with several microservices and will not work without the following:

- Auth Module
- Notification Module
- Mail Module
- Files Module

--- Override properties via environments variables

By default this service will try to connect to the default database, but database configurations
can be changed via environments variables.

Some environments variables possible values are:

DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
DATASOURCE_URL=jdbc:mysql://localhost:3306/core
DATASOURCE_USERNAME=user
DATASOURCE_PASSWORD=password
DATASOURCE_MAXIMUM_POOL_SIZE=1

docker run -p $AUTH_PORT:8080 \
    -e PROFILE=dev \
    -e DATASOURCE_URL=$DATASOURCE_URL \
    -e DATASOURCE_USERNAME=$DATASOURCE_USERNAME \
    -e DATASOURCE_PASSWORD=$DATASOURCE_PASSWORD \
    sharkno/dev:core