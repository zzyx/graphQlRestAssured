FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy Maven wrapper and make it executable
COPY mvnw ./
COPY .mvn .mvn/
RUN chmod +x mvnw

# Copy pom.xml and download dependencies (for better caching)
COPY pom.xml ./
RUN ./mvnw dependency:go-offline -DskipTests

# Copy source code
COPY src/ src/

# Default command runs CI profile tests
CMD ["./mvnw", "test", "-Pci"]