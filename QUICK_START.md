# Quick Start Guide - GraphQL API Tests

## 🚀 Running Tests

### Local Development
```bash
# Run all tests (default dev profile - verbose logging)
mvn clean test

# Run smoke tests only (fast)
mvn test -Psmoke

# Run with CI profile (parallel, minimal logging)
mvn test -Pci
```

### With Custom Configuration
```bash
# Override base URL
mvn test -Dgraphql.base.url=https://staging-api.com/graphql

# Override test user ID
mvn test -Dtest.user.id=42

# Enable verbose logging
mvn test -Dtest.enable.request.logging=true -Dtest.enable.response.logging=true
```

### Run Specific Tests
```bash
# Run single test class
mvn test -Dtest=TestGetCall

# Run single test method
mvn test -Dtest=TestGetCall#testGetCall

# Run multiple test classes
mvn test -Dtest=TestGetCall,TestPostCall
```

---

## ⚙️ Configuration

### Priority Order (Highest to Lowest)
1. **Environment Variables** (e.g., `GRAPHQL_BASE_URL`)
2. **System Properties** (e.g., `-Dgraphql.base.url=...`)
3. **Properties File** (`src/test/resources/test.properties`)
4. **Default Values** (hardcoded in `TestConfig.java`)

### Key Environment Variables
```bash
export GRAPHQL_BASE_URL=https://your-api.com/graphql
export TEST_USER_ID=21
export TEST_USERNAME=AdminUser1
export TEST_PASSWORD=Admin@User1
export TEST_AUTH_TOKEN=your-token-here
export TEST_ENABLE_REQUEST_LOGGING=false
export TEST_ENABLE_RESPONSE_LOGGING=true
```

---

## 📁 Project Structure

```
TestGraphQL/
├── src/test/java/
│   ├── base/                          # Base classes
│   │   ├── BaseGraphQLTest.java       # Base test class (extend this!)
│   │   └── GraphQLRequestBuilder.java # GraphQL request builder
│   ├── config/
│   │   └── TestConfig.java            # Configuration management
│   ├── utils/
│   │   └── TestDataGenerator.java     # Test data generation
│   ├── TestGetCall.java               # GET/Query tests
│   ├── TestPostCall.java              # POST/Create tests
│   ├── TestPatchCall.java             # PATCH/Update tests
│   ├── TestPatchCallWithGet.java      # Update with verification
│   ├── TestDeleteCall.java            # DELETE tests
│   ├── TestSignIn.java                # SignIn tests
│   └── TestSignUp.java                # SignUp tests
├── src/test/resources/
│   ├── test.properties                # Default configuration
│   └── testng.xml                     # TestNG suite configuration
├── .github/workflows/
│   └── api-tests.yml                  # GitHub Actions workflow
├── pom.xml                            # Maven configuration
├── .env.example                       # Environment variable template
├── TEST_EXECUTION_GUIDE.md            # Comprehensive guide
├── IMPROVEMENTS_SUMMARY.md            # All improvements documented
└── QUICK_START.md                     # This file
```

---

## 🧪 Writing New Tests

### Template
```java
import base.BaseGraphQLTest;
import base.GraphQLRequestBuilder;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestMyFeature extends BaseGraphQLTest {

    @Test
    public void testMyFeature() {
        log("Starting my test");
        
        // Build GraphQL request
        String requestBody = new GraphQLRequestBuilder()
                .queryOperation("myQuery")
                .argument("id", 123)
                .fields("id", "name")
                .build();
        
        // Execute request
        Response response = executeGraphQLRequest(requestBody);
        
        // Validate response
        assertValidResponse(response);
        
        // Extract and verify data
        Integer id = extractData(response, "data.myQuery.id");
        Assert.assertEquals(id, 123);
        
        log("Test completed successfully");
    }
}
```

### Using Test Data Generator
```java
import utils.TestDataGenerator;
import utils.TestDataGenerator.UserData;

@Test
public void testCreateUser() {
    // Generate unique test data
    UserData userData = TestDataGenerator.generateUserData("MyTest");
    
    String requestBody = new GraphQLRequestBuilder()
            .mutationOperation("createUser")
            .argument("firstName", userData.getFirstName())
            .argument("lastName", userData.getLastName())
            .argument("email", userData.getEmail())
            .fields("id", "firstName", "lastName")
            .build();
    
    Response response = executeGraphQLRequest(requestBody);
    assertValidResponse(response);
    
    // Track for automatic cleanup
    Integer userId = extractData(response, "data.createUser.id");
    trackCreatedUser(userId);
}
```

---

## 🔧 CI/CD Setup

### GitHub Actions (Already Configured!)
Just push to `main` or `develop` branch, or create a PR.

### GitLab CI
```yaml
test:
  stage: test
  image: maven:3.9-eclipse-temurin-21
  variables:
    GRAPHQL_BASE_URL: $GRAPHQL_BASE_URL
  script:
    - mvn clean test -Pci
  artifacts:
    reports:
      junit: target/surefire-reports/*.xml
```

### Jenkins
```groovy
pipeline {
    agent any
    environment {
        GRAPHQL_BASE_URL = credentials('graphql-url')
    }
    stages {
        stage('Test') {
            steps {
                sh 'mvn clean test -Pci'
            }
        }
    }
    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
    }
}
```

---

## 📊 Test Reports

### Location
```
target/surefire-reports/
├── index.html              # Open this in browser
├── TEST-*.xml              # JUnit XML for CI
└── *.txt                   # Text reports
```

### View Report
```bash
# Mac
open target/surefire-reports/index.html

# Linux
xdg-open target/surefire-reports/index.html

# Windows
start target/surefire-reports/index.html
```

---

## 🐛 Troubleshooting

### Tests Fail with Connection Timeout
```bash
mvn test -Dgraphql.connection.timeout=30000
```

### Tests Fail with "User not found"
```bash
mvn test -Dtest.user.id=<valid-user-id>
```

### Need More Logging
```bash
mvn test -X -Dtest.enable.request.logging=true
```

### Clean Build
```bash
mvn clean install -DskipTests
mvn test
```

---

## 📚 More Information

- **Comprehensive Guide**: See `TEST_EXECUTION_GUIDE.md`
- **All Improvements**: See `IMPROVEMENTS_SUMMARY.md`
- **Environment Variables**: See `.env.example`

---

## ✅ Key Features

- ✅ **Zero Configuration**: Works out of the box
- ✅ **Environment Flexible**: Easy to switch environments
- ✅ **Test Isolation**: Tests don't interfere with each other
- ✅ **Automatic Cleanup**: Created data is cleaned up automatically
- ✅ **Parallel Execution**: 3-5x faster with `-Pci`
- ✅ **Retry Mechanism**: Flaky tests are retried automatically
- ✅ **CI/CD Ready**: GitHub Actions, GitLab CI, Jenkins
- ✅ **Comprehensive Logging**: Configurable verbosity
- ✅ **Type Safe**: Configuration with proper types
- ✅ **Well Documented**: Multiple guides and examples

---

## 🎯 Common Commands Cheat Sheet

```bash
# Development
mvn test                                    # Run all tests (verbose)
mvn test -Psmoke                           # Quick smoke tests
mvn test -Dtest=TestGetCall                # Run specific test

# CI/CD
mvn test -Pci                              # Optimized for CI
mvn test -Pci -Dgraphql.base.url=...      # CI with custom URL

# Debugging
mvn test -X                                # Debug mode
mvn test -Dtest.enable.request.logging=true # Verbose logging

# Reports
mvn surefire-report:report                 # Generate HTML report
open target/surefire-reports/index.html    # View report

# Cleanup
mvn clean                                  # Clean build artifacts
mvn clean test                             # Clean and test
```

---

**Happy Testing! 🚀**

