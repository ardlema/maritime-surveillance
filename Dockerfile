FROM openjdk:8u181
ADD target/maritime-surveillance-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/maritime-surveillance-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/opt/maritime-surveillance-1.0-SNAPSHOT.jar"]