# Overview
This is a JAVA & RestAssured based test automation framework for REST API localed at https://gorest.co.in/.

## Features:
- Gradle is used as build tool.
- TestNG is used as a testing framework for specifying and running the tests.
- RestAssured library is used for validating the APIs.
- Reports are generated with TestNG support.

## How to run the tests
- Clone the repository from https://github.com/nidhi2627-git/acrolinx-qa-challenge.git
- Create a file "authToken.properties" containing "authToken" key and it's value as:
  - authToken= < value >
- Run gradle build that includes execution of tests. 
- Run `$./gradlew test` command later to only execute the tests.
- Detailed reports are generated at "./reports".