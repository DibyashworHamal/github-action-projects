FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="Dibyashwor Hamal"\
      version="1.0"\
      description="This is a simple Spring Boot maven application that connects to a MySQL database."
RUN addgroup -S ebsgroup && adduser -S ebsuser -G ebsgroup
WORKDIR /app
COPY --from=builder --chown=ebsuser:ebsgroup /app/target/*.jar ebs_app.jar
USER ebsuser
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 CMD sh -c "netstat -lnt | grep -q ':8081' || exit 1"'"
ENTRYPOINT ["java","-jar"]
CMD ["ebs_app.jar"]