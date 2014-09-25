#!/bin/bash

# Build:
mvn clean package && \

# Delete the old output:
rm -rf src/main/resources/taxonomy && \

# Generate the new output:
java $JAVA_OPTS -jar target/*-jar-with-dependencies.jar
