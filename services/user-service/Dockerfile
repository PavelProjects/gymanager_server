FROM alpine/git as repo
RUN git clone https://github.com/PavelProjects/gymanager_server /home/repo -b user_service_creation && cat /home/repo/pom.xml

# Build
FROM maven:3.6.0-jdk-11-slim AS build
COPY --from=repo /home/repo /home/app
RUN mvn -f /home/app/pom.xml clean package -pl :shared,:objects,:user-service -am -T 0.5C

# Run
FROM openjdk:11-jre-slim
COPY --from=build /home/app/services/user-service/target/*.jar /usr/local/lib/app.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]
EXPOSE 8080