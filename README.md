# Project 4WT - Adv programming topics of microservices with Spring Boot

### 1. INTRODUCTION
**Team members:** Michal Davidse (r0809700) & Kevin Segers (r0793406)

_Background information_  
The application shows the items on a page in AR. To start the user opens the app and has to choose a category. 
Per category there are several books. When a book is chosen the camera opens and scans the page. The items on the page will appear!
The frond end of the application was made using flutter and wikitude. 
The models were made using Sketchup.

_Introduction & Documentation_  
This repository contains the project of Advanced programming topics of coding microservices using Spring Boot.
One _Edge service_ `brank-edge-service` will connect to two lower services `book-service` and `page-service` to request information which it will then process and combine into a single response to the user. 
The user is only supposed to communicate with the `brank-edge-service`.

Link to `book-service` repository:  [book-service](https://github.com/KevinSegers/nijntje-book-service)  
Link to `page-service` repository:  [page-service](https://github.com/KevinSegers/nijntje-page-service) 


The example architecture is as follows:

![alt text](https://github.com/KevinSegers/project-ar-backend/blob/experimental/SchemaProject.png?raw=true)


### 2. SET UP DOCKER CONTAINERS

#### Set up the Docker container with the MySQL database:

`docker run --name books-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=abc123 -d mysql `

#### Set up the Docker container with the MongoDB database:

`docker run --name pages-mongodb -p 27017-27019:27017-27019 -d mongo`


### 3. SERVICES