FROM alpine/git as repo
RUN git clone https://github.com/PavelProjects/gymanager_server /home/repo -b gateway && cat /home/repo/pom.xml

# Build
FROM maven:3.8.3-openjdk-17 AS build
COPY --from=repo /home/repo /home/app
RUN mvn -f /home/app/pom.xml clean package -pl :shared,:user-service -am -T 0.5C

# Run
FROM openjdk:17.0.1-jdk-slim
COPY --from=build /home/app/services/user-service/target/*.jar /usr/local/lib/app.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]
EXPOSE 8080