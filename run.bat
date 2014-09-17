

set JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

set RESTOLINO_STATIC="src/main/resources/files"
set RESTOLINO_CLASSES="target/classes"


REM # Elasticsearch
set BONSAI_URL=http://localhost:9200


mvn clean package && java %JAVA_OPTS% -Drestolino.files=%RESTOLINO_STATIC% -Drestolino.classes=%RESTOLINO_CLASSES% -cp "target/dependency/*" com.github.davidcarboni.restolino.Main


# Development: reloadable
#java %JAVA_OPTS% -Drestolino.files=%RESTOLINO_STATIC% -Drestolino.classes=%RESTOLINO_CLASSES% -cp "target/dependency/*" com.github.davidcarboni.restolino.Main

# Production: non-reloadable
#java %JAVA_OPTS% -jar target/*-jar-with-dependencies.jar
