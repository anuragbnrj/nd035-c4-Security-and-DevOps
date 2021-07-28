FROM openjdk:8-jre
COPY target/ecommerce-application-0.0.1-SNAPSHOT.jar ecommerce-application-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/ecommerce-application-0.0.1-SNAPSHOT.jar"]

