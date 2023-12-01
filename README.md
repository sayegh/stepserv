# StepServ

A boilerplate CRUD application server skeleton to store and retrieve metaverse avatar data.
Build is maven-based.

It uses Spring Boot, Postgres via JPA and can be run as Docker container.

## Build

The build process also runs JUnit tests and produces a Jacoco test coverage report.

````
mvn clean package
````

## Run

The application requires a Postgres database to be reachable at
````
jdbc:postgresql://localhost:5432/stepserv
````

### Locally
```` 
export JWT_SECRET=<some secret>
mvn spring-boot:run
````

## Deploy

