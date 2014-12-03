REM #!/bin/bash

set JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

rem # Restolino configuration
set RESTOLINO_STATIC="src/main/resources/files"
set RESTOLINO_CLASSES="target/classes"
set PACKAGE_PREFIX=com.github.onsdigital

rem # Elasticsearch
set BONSAI_URL=http://localhost:9200
set BONSAI_CLUSTERNAME=elasticsearch
set BONSAI_HOSTNAME=localhost
set BONSAI_TRANSPORT_PORT=9300

# Mongodb
export MONGO_USER=ons
export MONGO_PASSWORD=uJlVY2FDGI5SFawS/PN+jnZpymKWpU7C

rem # Generate taxonomy
rem #mvn clean compile dependency:copy-dependencies && ^
rem #rm -rf src/main/taxonomy && ^
rem #java -cp "target/classes:target/dependency/*" com.github.onsdigital.generator.TaxonomyGenerator

rem # Build and run:
mvn -Dmaven.test.skip=true clean compile dependency:copy-dependencies && ^
java %JAVA_OPTS% ^
 -Drestolino.files=%RESTOLINO_STATIC% ^
 -Drestolino.classes=%RESTOLINO_CLASSES% ^
 -Drestolino.packageprefix=%PACKAGE_PREFIX% ^
 -Dmongo.user=$MONGO_USER ^
 -Dmongo.password=$MONGO_PASSWORD ^
 -cp "target/dependency/*" ^
 com.github.davidcarboni.restolino.Main
 
