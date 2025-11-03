FROM maven:3.9-eclipse-temurin-25

WORKDIR /app

# Copy pom.xml and download dependencies (for better caching)
COPY pom.xml ./
RUN mvn dependency:go-offline -DskipTests

# Copy source code
COPY src/ src/

# Default command runs CI profile tests
CMD ["mvn", "test", "-Pci"]