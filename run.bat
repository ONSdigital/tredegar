REM #!/bin/bash

set JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

rem # Restolino configuration
set RESTOLINO_STATIC="src/main/resources/files"
rem set RESTOLINO_STATIC="target/classes/files"
set RESTOLINO_CLASSES="target/classes"
set PACKAGE_PREFIX=com.github.onsdigital

rem # Elasticsearch
set BONSAI_URL=http://localhost:9200
set BONSAI_CLUSTERNAME=elasticsearch
set BONSAI_HOSTNAME=localhost
set BONSAI_TRANSPORT_PORT=9300

rem # Generate taxonomy
rem #mvn clean compile dependency:copy-dependencies && ^
rem #rm -rf src/main/taxonomy && ^
rem #java -cp "target/classes:target/dependency/*" com.github.onsdigital.generator.Csv

rem # Now build the JAR:
mvn -Dmaven.test.skip=true package && ^
rem # Development: reloadable ^
java $JAVA_OPTS -Drestolino.files=$RESTOLINO_STATIC -Drestolino.classes=$RESTOLINO_CLASSES -Drestolino.packageprefix=$PACKAGE_PREFIX -cp "target/dependency/*" com.github.davidcarboni.restolino.Main

rem # Production: non-reloadable
rem #java $JAVA_OPTS -jar target/*-jar-with-dependencies.jar
