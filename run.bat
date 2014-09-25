

set JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

set RESTOLINO_STATIC="src/main/resources/files"
set RESTOLINO_CLASSES="target/classes"


REM # Elasticsearch
set BONSAI_URL=http://localhost:9200

REM # Build ^
mvn clean package && ^
 ^
REM # Generate taxonomy ^
java -cp "target/classes:target/dependency/*" com.github.onsdigital.generator.Csv && ^
 ^
REM # Development: reloadable ^
java %JAVA_OPTS% -Drestolino.files=%RESTOLINO_STATIC% -Drestolino.classes=%RESTOLINO_CLASSES% -cp "target/dependency/*" com.github.davidcarboni.restolino.Main

REM # Production: non-reloadable
REM #java %JAVA_OPTS% -jar target/*-jar-with-dependencies.jar
