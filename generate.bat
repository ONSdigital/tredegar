@echo off

REM # Build:
mvn clean package

REM # Delete the old output:
echo What's this in Windows Latin?
echo rm -rf src/main/taxonomy

REM # Generate the new output:
java -cp "target/classes:target/dependency/*.jar" com.github.onsdigital.generator.Csv
