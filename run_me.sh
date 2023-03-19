#!/bin/bash

# Set the name of your Spring Boot project's JAR file
JAR_NAME="mangekyo-mock-server-0.0.1.jar"

# Clean up any old JAR files
rm -f $JAR_NAME

# Build the Spring Boot project with Maven
./mvnw clean package

# Run the JAR file
java -jar target/$JAR_NAME
