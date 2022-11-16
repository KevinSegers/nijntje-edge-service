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
The user is only supposed to communicate with the `brank-edge-service`.  We used github-actions to: docker-hub, sonarcloud and docker compose to deploy the project on okteto cloud.


Link to `edge-service` repository:  [edge-service](https://github.com/KevinSegers/nijntje-edge-service)   
Link to `book-service` repository:  [book-service](https://github.com/KevinSegers/nijntje-book-service)  
Link to `page-service` repository:  [page-service](https://github.com/KevinSegers/nijntje-page-service)  
Link to `docker-compose` repository:  [docker-compose](https://github.com/KevinSegers/nijntje-docker-compose) 


The example architecture is as follows:

![](readmeImages/SchemaProject.png)

### 2. SET UP DOCKER CONTAINERS

#### 2.1 Local Set up

**Set up the Docker container with the MySQL database:**

`docker run --name books-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=abc123 -d mysql `

**Set up the Docker container with the MongoDB database:**

`docker run --name pages-mongodb -p 27017-27019:27017-27019 -d mongo`

**Run the local set up**

#### 2.2 Run docker compose

Run the docker compose via following url: [docker-compose](https://github.com/KevinSegers/nijntje-docker-compose)) 


### 3. SERVICES
#### 3.1 Book-service

**_3.1.1 End points_**


![](readmeImages/BookSwagger.png)

+ `GET /books`&emsp; Get all books   
+ `POST /books`&emsp; Add book
+ `PUT /books` &emsp; Update book
+ `GET /books/title/{title}`&emsp; Get book by title
+ `GET /books/category/{category}`&emsp; Get all books by category
+ `DELETE  /books/{booktitle}`&emsp; Delete book   

**_3.1.2 Testing_**   
Coverage unit testing: 
![](readmeImages/Testing/BookControllerUnitTests.png)

Coverage integration testing: 
![](readmeImages/Testing/BookControllerIntegrationTests.png)



#### 3.2 Page-service

**_3.2.1 End points_**

![](readmeImages/PageSwagger.png)

+ `GET /pages` &emsp; Get all pages
+ `POST /pages`&emsp;Add a page
+ `PUT /pages` &emsp; Update a page
+ `GET /pages/booktitle/{bookTitle}` &emsp; Get all pages from book
+ `DELETE /pages/booktitle/{bookTitle}/pagenumber/{pageNumber}` &emsp; Delete a page
+ `GET /pages/booktitle/{bookTitle}/pagenumber/{pageNumber}" ` &emsp;   Get one page by booktitle and pagenumber
+ `GET /pages/booktitle/{bookTitle}/pagenumber/{pageNumber}/items` &emsp; Get all items from a page
+ `GET /pages/booktitle/{bookTitle}/pagesseen` &emsp; Get seen pages in double
+ `PUT /pages/booktitle/{bookTitle}/setpagesunseen` &emsp; Set all pages of a book unseen

**_3.2.2 Testing_**  
Coverage unit testing:  
![](readmeImages/Testing/PageControllerUnitTests.png)

Coverage integration testing:  
![](readmeImages/Testing/PageControllerIntegrationTest.png)



#### 3.3 Edge-service
**_3.3.1 End points_**
![](readmeImages/EdgeSwagger.png)  
&nbsp;
+ `GET /interactivebooks/book/{bookTitle}`   
Get Book with pages  
![](readmeImages/Edge Service/Postman/9 getBookWithPages.png)
&nbsp; 

+ `GET /interactivebooks/books/{bookTitle}/pagesseen`  
Get pages seen in decimal  
![](readmeImages/Edge Service/Postman/7 getBookPagesSeen.png)  
&nbsp;

+ `PUT /interactivebooks/books/{bookTitle}/setpagesunseen`  
  Set pages from book unseen  
  ![](readmeImages/Edge Service/Postman/4 setBookPagesUnseen.png)
  &nbsp;

+ `GET /interactivebooks/booksbycategory/{category}`  
  Get books for category  
  ![](readmeImages/Edge Service/Postman/2 getBooksByCategory.png)
  &nbsp;

+ `GET /interactivebooks/booktitle/{bookTitle}/pagenumber/{pageNumber}/items`  
  Get items from page
  ![](readmeImages/Edge Service/Postman/6 getItemsFromPage.png)
  &nbsp;

+ `GET /interactivebooks/categorieswithurls`  
Get categories with url  
![](readmeImages/Edge Service/Postman/1 getCategorieswithUrls.png)
&nbsp;

+ `POST /interactivebooks/pages`  
  Add page to bookwithPages  
  ![](readmeImages/Edge Service/Postman/11 addPage.png)
  &nbsp;

+ `PUT /interactivebooks/pages`  
  Update page from Book with page
  ![](readmeImages/Edge Service/Postman/10 updatePage.png)
  &nbsp;

+ `DELETE /interactivebooks/pages/booktitle/{bookTitle}/pagenumber/{pageNumber}`  
  Delete page  
  ![](readmeImages/Edge Service/Postman/12 deletePage.png)
  &nbsp;


+ `GET /interactivebooks/pages/booktitle/{bookTitle}/pagenumber/{pageNumber}`  
Get page by booktitle and pagenumber
![](readmeImages/Edge Service/Postman/5 getPageByBookTitleAndPageNumber.png)
&nbsp;


+ `PUT /interactivebooks/pages/booktitle/{bookTitle}/pagenumber/{pageNumber}`  
Update page to seen given booktitle and pagenumber
![](readmeImages/Edge Service/Postman/8 updatePageSeen.png)
&nbsp;

+ `PUT /interactivebooks/updatebook`  
  Update book
  ![](readmeImages/Edge Service/Postman/3 updatebook.png)
  &nbsp;

**_3.3.2 Testing_**  
coverage unit testing:
![](readmeImages/Testing/BookWithPagesUnitTests.png)

