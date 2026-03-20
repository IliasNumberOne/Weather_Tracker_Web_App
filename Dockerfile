# 1. Сборка проекта (build stage)
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests


# 2. Runtime (Tomcat)
FROM tomcat:10.1-jdk17

# удаляем дефолтные приложения
RUN rm -rf /usr/local/tomcat/webapps/*

# копируем war
COPY --from=build /app/target/Weather.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]