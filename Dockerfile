# Build stage
FROM maven:3.8.4-openjdk-11-slim AS builder

# Update to use newer Debian repos
RUN echo "deb http://deb.debian.org/debian bullseye main" > /etc/apt/sources.list && \
    echo "deb http://security.debian.org/debian-security bullseye-security main" >> /etc/apt/sources.list && \
    echo "deb http://deb.debian.org/debian bullseye-updates main" >> /etc/apt/sources.list

# Install required packages
RUN apt-get update && \
    apt-get install -y aapt git && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .

# Build the application
RUN mvn clean install -DskipTests

# Runtime stage
FROM tomcat:9.0-jre11-temurin-jammy

# Install aapt (using Ubuntu repositories since this is Ubuntu-based)
RUN apt-get update && \
    apt-get install -y aapt && \
    rm -rf /var/lib/apt/lists/*

# Copy the built war file - Changed from server.war to launcher.war
COPY --from=builder /app/server/target/launcher.war /usr/local/tomcat/webapps/ROOT.war
COPY --from=builder /app/server/build.properties /usr/local/tomcat/build.properties

# Create required directories
RUN mkdir -p /tmp/launcher/files /tmp/launcher/plugins && \
    chmod -R 777 /tmp/launcher

EXPOSE 8080
CMD ["catalina.sh", "run"]
