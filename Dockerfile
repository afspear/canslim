FROM maven:3.5

WORKDIR /code

# Prepare by downloading dependencies
ADD pom.xml /code/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /code/src
RUN ["mvn", "package"]

CMD ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "target/canslim-1.0-SNAPSHOT-jar-with-dependencies.jar"]