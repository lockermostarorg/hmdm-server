# Build stage
FROM maven:3.8.4-openjdk-11-slim AS builder

# Install required packages
RUN apt-get update && apt-get install -y aapt git

WORKDIR /app
COPY . .

# Build the application
RUN mvn clean install -DskipTests

# Runtime stage
FROM tomcat:9-jre11-slim

# Install aapt
RUN apt-get update && apt-get install -y aapt

# Copy the built war file
COPY --from=builder /app/server/target/server.war /usr/local/tomcat/webapps/ROOT.war
COPY --from=builder /app/server/build.properties /usr/local/tomcat/build.properties

# Create required directories
RUN mkdir -p /tmp/launcher/files /tmp/launcher/plugins

EXPOSE 8080
CMD ["catalina.sh", "run"]
