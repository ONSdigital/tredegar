#!/bin/bash

export JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

# Restolino configuration
export RESTOLINO_STATIC="target/classes/files"
export RESTOLINO_CLASSES="target/classes"
export PACKAGE_PREFIX=com.github.onsdigital

# Elasticsearch
export BONSAI_URL=http://localhost:9200
export BONSAI_CLUSTERNAME=elasticsearch
export BONSAI_HOSTNAME=localhost
export BONSAI_TRANSPORT_PORT=9300


#External Taxonomy
export TAXONOMY_DIR=/home/boorhun/Desktop/taxonomy

#Generate taxonomy
mvn clean compile dependency:copy-dependencies && \
rm -rf src/main/taxonomy && \
java -cp "target/classes:target/dependency/*" com.github.onsdigital.generator.Csv

# Now build the JAR:
mvn package && \

    if hash heroku 2>/dev/null; then
    
    	# Run Heroku style if the Heroku Toolbelt is installed
        foreman start
        
    else

		# Otherwise go direct to Java
		
		# Development: reloadable
		java $JAVA_OPTS -Drestolino.files=$RESTOLINO_STATIC -Drestolino.classes=$RESTOLINO_CLASSES -Drestolino.packageprefix=$PACKAGE_PREFIX -cp "target/dependency/*" com.github.davidcarboni.restolino.Main
		
		# Production: non-reloadable
		#java $JAVA_OPTS -jar target/*-jar-with-dependencies.jar

    fi
