# Conference SpringBoot application

**The purpose of this repository is to show a legacy Java application, which then can be migrated to Kotlin.**

## Domain
![Domain diagram](docs/domain-diagram.png)
* Only one conference with a specific name at a time
* Maximal number of attendees cannot be exceeded
* Each attendee may register for a given conference only once

## Access points
* Backend-Server is running on http://localhost:8080. Use the [./generated-requests.http](Request file) file to send requests.
* REST-API description is available via [Swagger](http://localhost:8080/swagger-ui/index.html)
* Database is available via [H2-Console](http://localhost:8080/h2) (use "jdbc:h2:mem:konfi" as JDBC-URL)

## Project structure
* Module `backend` contains the 'legacy' Java backend.
* Module `solution-backend` contains migrated and polished Kotlin project.
* Module `solution-frontend` contains an awesome frontend written in Kotlin/JS!

### Requirements
* Java 11+
* *Optional: Node 14 for frontend*

## Testing & running
Konfi can be tested and started by executing Gradle tasks:
* Test with `./gradlew backend:test`
* Run with `./gradlew backend:bootRun`

To run the frontend in the `kotlin` branch use `./gradlew solution-frontend:browserDevelopmentRun`

