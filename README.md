

`TEXT Search Counter API`
=====================


A secure Springboot based RESTful application providing APIs to search one or more text (string) inside a given paragraph of text.

### Requirements
This applications should allow users to:

    1. Search count for each of the texts (strings) input as a list
        
        - Expected => JSON with counts of each search string 
        
    2. Search top N texts (strings)
    
        - Expected => CSV with a list of top N strings and their counts
        
Note : The APIs to be secured with Spring Security

---------

Technical Solution
-----

- Java 8
- Spring Boot
- Spring Security
- OpenCSV
- Kafka
- Junit 4
- Docker
- Docker Compose

How to use
----

The application can be started with a command line script:

- `sh bin/run.sh`

This script fires up all the required docker containers:

- `text-counter-app`: Spring Boot app to process text search in a given paragraph (uploaded as a .txt file)   
- `text-counter-kafka` : Kafka single node cluster - To process paragraph file (.txt) upload Asynchronously
- `text-counter-zookeeper` : Zookeeper service to keep track of Kafka nodes, topics, etc

The application can be run in 2 modes:

1. `Prod / Docker mode` : the script `bin/run.sh` starts up everything needed - runs at localhost:9090
2. `Dev mode` : run from project root using - `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` - runs at localhost:8080

The application exposes the following endpoints:

| HTTP Method  | Endpoint                       | Secured (Basic Auth)  | User Role | Description                                                   |
|:-------------|:-------------------------------|:----------------------|:----------|:--------------------------------------------------------------| 
| GET          | /counter-api/                  | No                    | N/A       | Welcome message                                               |
| POST         | /counter-api/search            | Yes                   | USER      | Search count for each of the texts (strings) input as a list  |
| GET          | /counter-api/top/{numRecords}  | Yes                   | USER      | Search top N texts (strings)                                  |
| POST         | /counter-api/upload            | Yes                   | ADMIN     | Upload Input paragraph text file s(.txt)                      |


API Security Setup:

| User Role     | Credentials         | Authorization Header         |
|:--------------|:--------------------|:-----------------------------|
| USER          | `user:pwd`          | `Basic dXNlcjpwd2Q=`         |
| ADMIN         | `admin:password`    | `Basic YWRtaW46cGFzc3dvcmQ=` |


The application logs are generated at:

- `logs/app.log` 

The input and output files are stored at:

- `data/downloads` : generated output CSV file
- `data/uploads` : input file uploaded by the user


Examples: 
----

1. Search the following texts, which will return the counts respectively:

        Sample Request
        > curl http://localhost:9090/counter-api/search -H"Authorization: Basic b3B0dXM6Y2FuZGlkYXRlcw==" -d’{“searchText”:[“Duis”, “Sed”, “Donec”, “Augue”, “Pellentesque”, “123”]}’ -H"Content-Type: application/json" –X POST
        
        Result in JSON:
        > {"counts": [{"Duis": 11}, {"Sed": 16}, {"Donec": 8}, {"Augue": 7}, {"Pellentesque": 6},{"123": 0}]}

2. Provide the top 20 (as Path Variable) Texts, which has the highest counts in the Sample Paragraphs provided:

        Sample Request
        > curl http://localhost:9090/counter-api/top/20 -H"Authorization: Basic b3B0dXM6Y2FuZGlkYXRlcw==" -H”Accept: text/csv”
        
        Result: If I put /top/5:
        text1|100
        text2|91
        text3|80
        text4|70
        text5|60


