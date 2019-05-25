# Getting Started

###Introduction
App to manage user info in a DB and provides user management operations such as Create, Delete, Retrieve etc

This App uses MongoDB as data store.

### DataBase config
'spring.data.mongodb.uri' property in application.properties should be updated with the mongo DB URI that this app can use.

Note that the user used to establish connection should have access to create DB,collection and indexes. This App will create a DB 'userdata', collection under the DB named 'users' and the required indexes.

To install mongo server in a local machine:
* [Mongo installation guide](https://docs.mongodb.com/manual/installation/)

### To start the app
To start the app, run maven command`spring-boot:run` from the directory containing pom.xml

### Test coverage report
To generate test coverage report, run maven command `mvn test` from the directory containing pom.xml, coverage report will 
be found under  `target/site/jacoco/index.html`

### Integration tests using postman
Postman tests are checked-in under _src/test/postman/positive-tests.postman_collection.json_ and C:\Users\chandrasekar\IdeaProjects\users-curd\src\test\postman\negative-tests.postman_collection.json

In order to run the tests, please setup a env and add 'baserl', this should be updated to the endpoint where this app accessible




