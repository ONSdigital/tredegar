#!/bin/bash

# Build:
mvn clean package && \

# Delete the old output:
rm -rf src/main/taxonomy && \

# Generate the new output:
java -cp "target/classes:target/dependency/*.jar" com.github.onsdigital.generator.Csv
