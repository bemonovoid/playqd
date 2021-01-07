gradlew.bat clean bootJar

java -jar -Dspring.profiles.active=prod *.jar