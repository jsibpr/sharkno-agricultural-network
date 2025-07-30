  ______ _ _              _____                 _          
 |  ____(_) |            / ____|               (_)         
 | |__   _| | ___  ___  | (___   ___ _ ____   ___  ___ ___ 
 |  __| | | |/ _ \/ __|  \___ \ / _ \ '__\ \ / / |/ __/ _ \
 | |    | | |  __/\__ \  ____) |  __/ |   \ V /| | (_|  __/
 |_|    |_|_|\___||___/ |_____/ \___|_|    \_/ |_|\___\___|
                                                                                                            
                                                           
--- Build with Docker ---

docker build . -t files

--- Run with Docker ---

docker run -p 8080:8080 files

--- Run with docker with non-default profile (prod profile example) --

docker run -p 8080:8080 -e PROFILE=prod files

--- Run project with application.properties + profile overrides (dev example)

On your IDE, on java VM options add the following argument:

-Dspring.profiles.active=dev

To access an S3 repository the host has to be configured with following environment variables.

AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY

Alternatively, if the application is running on an EC2 the role of the host has to have permission to acces the S3 bucket.