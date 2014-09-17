#!/bin/bash

export JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

# Restolino configuration
export RESTOLINO_STATIC="src/main/resources/files"
export RESTOLINO_CLASSES="target/classes"
export PACKAGE_PREFIX=uk.co.methods

# Elasticsearch
export BONSAI_URL=http://localhost:9200

mvn clean package && \

# Development: reloadable
java $JAVA_OPTS -Drestolino.files=$RESTOLINO_STATIC -Drestolino.classes=$RESTOLINO_CLASSES -Drestolino.packageprefix=$PACKAGE_PREFIX -cp "target/dependency/*" com.github.davidcarboni.restolino.Main

# Production: non-reloadable
#java $JAVA_OPTS -jar target/*-jar-with-dependencies.jar
