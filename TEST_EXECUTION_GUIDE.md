# GraphQL API Test Execution Guide

## Table of Contents
- [Overview](#overview)
- [Configuration](#configuration)
- [Running Tests Locally](#running-tests-locally)
- [Running Tests in CI/CD](#running-tests-in-cicd)
- [Test Profiles](#test-profiles)
- [Environment Variables](#environment-variables)
- [Test Reports](#test-reports)
- [Troubleshooting](#troubleshooting)

## Overview

This test suite provides comprehensive testing for GraphQL API operations including:
- Query operations (GET)
- Mutation operations (POST, PATCH, DELETE)
- Authentication (SignIn, SignUp)

### Key Features
- ✅ Centralized configuration management
- ✅ Environment variable support for CI/CD
- ✅ Automatic test data cleanup
- ✅ Parallel test execution
- ✅ Retry mechanism for flaky tests
- ✅ Comprehensive logging
- ✅ Multiple test profiles (dev, ci, smoke)

## Configuration

### Configuration Priority
The test framework uses the following priority for configuration values:
1. **Environment Variables** (highest priority)
2. **System Properties** (`-Dproperty=value`)
3. **Properties File** (`src/test/resources/test.properties`)
4. **Default Values** (lowest priority)

### Properties File
Default configuration is in `src/test/resources/test.properties`:
```properties
graphql.base.url=https://graphql-api-ppql.onrender.com/graphql
test.user.id=21
test.username=AdminUser1
# ... etc
```

### Environment Variables
For CI/CD, use environment variables (see `.env.example`):
```bash
export GRAPHQL_BASE_URL=https://your-api-url.com/graphql
export TEST_USER_ID=21
export TEST_USERNAME=AdminUser1
```

## Running Tests Locally

### Prerequisites
- Java 25 (or compatible version)
- Maven 3.6+

### Run All Tests
```bash
mvn clean test
```

### Run with Development Profile (verbose logging)
```bash
mvn clean test -Pdev
```

### Run Smoke Tests Only
```bash
mvn clean test -Psmoke
```

### Run Specific Test Class
```bash
mvn test -Dtest=TestGetCall
```

### Run Specific Test Method
```bash
mvn test -Dtest=TestGetCall#testGetCall
```

### Override Configuration via Command Line
```bash
mvn test -Dgraphql.base.url=https://staging-api.com/graphql -Dtest.user.id=42
```

## Running Tests in CI/CD

### GitHub Actions Example
```yaml
name: API Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 25
        uses: actions/setup-java@v3
        with:
          java-version: '25'
          distribution: 'temurin'
      
      - name: Run Tests
        env:
          GRAPHQL_BASE_URL: ${{ secrets.GRAPHQL_BASE_URL }}
          TEST_USERNAME: ${{ secrets.TEST_USERNAME }}
          TEST_PASSWORD: ${{ secrets.TEST_PASSWORD }}
          TEST_AUTH_TOKEN: ${{ secrets.TEST_AUTH_TOKEN }}
        run: mvn clean test -Pci
      
      - name: Publish Test Report
        uses: dorny/test-reporter@v1
        if: always()
        with:
          name: Test Results
          path: target/surefire-reports/*.xml
          reporter: java-junit
```

### GitLab CI Example
```yaml
test:
  stage: test
  image: maven:3.9-eclipse-temurin-21
  variables:
    GRAPHQL_BASE_URL: "https://graphql-api-ppql.onrender.com/graphql"
    TEST_ENABLE_REQUEST_LOGGING: "false"
    TEST_ENABLE_RESPONSE_LOGGING: "false"
  script:
    - mvn clean test -Pci
  artifacts:
    when: always
    reports:
      junit: target/surefire-reports/*.xml
    paths:
      - target/surefire-reports/
```

### Jenkins Pipeline Example
```groovy
pipeline {
    agent any
    
    environment {
        GRAPHQL_BASE_URL = credentials('graphql-base-url')
        TEST_USERNAME = credentials('test-username')
        TEST_PASSWORD = credentials('test-password')
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
            publishHTML([
                reportDir: 'target/surefire-reports',
                reportFiles: 'index.html',
                reportName: 'Test Report'
            ])
        }
    }
}
```

## Test Profiles

### Development Profile (`dev`)
**Default profile** - Activated automatically for local development
```bash
mvn test -Pdev
```
- Sequential test execution (thread-count=1)
- No retry on failures
- Verbose logging enabled
- Best for debugging

### CI Profile (`ci`)
**Optimized for CI/CD environments**
```bash
mvn test -Pci
```
- Parallel execution (thread-count=5)
- Retry failed tests twice
- Minimal logging
- Faster execution

### Smoke Profile (`smoke`)
**Quick validation tests**
```bash
mvn test -Psmoke
```
- Runs only tests tagged with `@Test(groups = "smoke")`
- Fast feedback loop
- Ideal for pre-commit hooks

## Environment Variables

### Complete List

| Variable | Description | Default |
|----------|-------------|---------|
| `GRAPHQL_BASE_URL` | GraphQL API endpoint | `https://graphql-api-ppql.onrender.com/graphql` |
| `GRAPHQL_CONTENT_TYPE` | Request content type | `application/json` |
| `GRAPHQL_CONNECTION_TIMEOUT` | Connection timeout (ms) | `10000` |
| `GRAPHQL_REQUEST_TIMEOUT` | Request timeout (ms) | `30000` |
| `TEST_USER_ID` | Test user ID for updates | `21` |
| `TEST_USERNAME` | Test username | `AdminUser1` |
| `TEST_PASSWORD` | Test password | `Admin@User1` |
| `TEST_AUTH_TOKEN` | Test auth token | (see .env.example) |
| `TEST_ENABLE_REQUEST_LOGGING` | Enable request logging | `false` |
| `TEST_ENABLE_RESPONSE_LOGGING` | Enable response logging | `true` |

### Setting Environment Variables

**Linux/Mac:**
```bash
export GRAPHQL_BASE_URL=https://your-api.com/graphql
```

**Windows (CMD):**
```cmd
set GRAPHQL_BASE_URL=https://your-api.com/graphql
```

**Windows (PowerShell):**
```powershell
$env:GRAPHQL_BASE_URL="https://your-api.com/graphql"
```

## Test Reports

### Surefire Reports
After running tests, reports are generated in:
```
target/surefire-reports/
├── index.html              # HTML report
├── TEST-*.xml              # JUnit XML reports
└── *.txt                   # Text reports
```

### View HTML Report
```bash
open target/surefire-reports/index.html  # Mac
xdg-open target/surefire-reports/index.html  # Linux
start target/surefire-reports/index.html  # Windows
```

### Generate Report Manually
```bash
mvn surefire-report:report
```

## Troubleshooting

### Tests Fail with Connection Timeout
**Solution:** Increase timeout values
```bash
mvn test -Dgraphql.connection.timeout=30000 -Dgraphql.request.timeout=60000
```

### Tests Fail Due to Missing User ID
**Solution:** Update test user ID
```bash
mvn test -Dtest.user.id=<valid-user-id>
```

### Parallel Execution Issues
**Solution:** Reduce thread count or disable parallel execution
```bash
mvn test -Dparallel.tests=1
```

### View Full Stack Traces
**Solution:** Run with `-X` flag
```bash
mvn test -X
```

### Clean Build Issues
**Solution:** Clean and rebuild
```bash
mvn clean install -DskipTests
mvn test
```

### Enable Debug Logging
**Solution:** Set logging environment variables
```bash
export TEST_ENABLE_REQUEST_LOGGING=true
export TEST_ENABLE_RESPONSE_LOGGING=true
mvn test
```

## Best Practices for CI/CD

1. **Use Secrets Management**: Store sensitive data (passwords, tokens) in CI/CD secrets
2. **Set Appropriate Timeouts**: Adjust based on your API response times
3. **Use Retry Mechanism**: Enable retries for flaky tests in CI
4. **Parallel Execution**: Use parallel execution to speed up test runs
5. **Fail Fast**: Configure `testFailureIgnore=false` to fail builds on test failures
6. **Archive Reports**: Always archive test reports as artifacts
7. **Health Checks**: Add API health check before running tests
8. **Environment Isolation**: Use separate test data for different environments

## Example: Complete CI Setup

```bash
# 1. Set environment variables
export GRAPHQL_BASE_URL=https://staging-api.com/graphql
export TEST_USERNAME=ci_user
export TEST_PASSWORD=ci_password

# 2. Run smoke tests first
mvn test -Psmoke

# 3. If smoke tests pass, run full suite
mvn test -Pci

# 4. Generate and publish reports
mvn surefire-report:report
```

## Support

For issues or questions:
1. Check the test logs in `target/surefire-reports/`
2. Review the configuration in `src/test/resources/test.properties`
3. Verify environment variables are set correctly
4. Run with debug logging enabled

