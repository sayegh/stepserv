# StepServ

> [!NOTE]
> This project is work in progress. I may or may not work properly in your environment,
> can have an intermediate state etc.

A boilerplate CRUD application server skeleton to store and retrieve metaverse avatar data.
Build is maven-based.

It uses Spring Boot, Postgres via JPA and can be run as Docker container.

## Build

The build process also runs JUnit tests and produces a Jacoco test coverage report.

````
mvn clean package
````

Run Jacoco test coverage checks:

````
mvn verify
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

