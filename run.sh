#!/bin/bash

export JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

# Restolino configuration
export RESTOLINO_STATIC="src/main/resources/files"
export RESTOLINO_CLASSES="target/classes"
export PACKAGE_PREFIX=com.github.onsdigital

# Elasticsearch
export BONSAI_URL=http://localhost:9200

# Compile the code:
mvn clean compile dependency:copy-dependencies && \

# Generate taxonomy
rm -rf src/main/taxonomy && \
java -cp "target/classes:target/dependency/*" com.github.onsdigital.generator.Csv

# Now build the JAR:
mvn package && \

# Development: reloadable
java $JAVA_OPTS -Drestolino.files=$RESTOLINO_STATIC -Drestolino.classes=$RESTOLINO_CLASSES -Drestolino.packageprefix=$PACKAGE_PREFIX -cp "target/dependency/*" com.github.davidcarboni.restolino.Main

# Production: non-reloadable
#java $JAVA_OPTS -jar target/*-jar-with-dependencies.jar
