package base;

import config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * Base class for all GraphQL API tests.
 * Provides common setup, configuration, and utility methods.
 */
public abstract class BaseGraphQLTest {

    protected TestConfig config;
    protected RequestSpecification requestSpec;
    
    // Track created resources for cleanup
    protected List<Integer> createdUserIds = new ArrayList<>();
    protected String currentTestName;

    /**
     * One-time setup before all tests in the class.
     * Configures RestAssured and loads test configuration.
     */
    @BeforeClass
    public void setUpClass() {
        config = TestConfig.getInstance();
        
        // Print configuration for debugging (especially useful in CI)
        config.printConfiguration();
        
        // Configure RestAssured base settings
        RestAssured.baseURI = config.getBaseUrl();
        
        // Build request specification
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setContentType(config.getContentType());

        // Add logging filters based on configuration
        if (config.isRequestLoggingEnabled()) {
            builder.addFilter(new RequestLoggingFilter());
        }
        if (config.isResponseLoggingEnabled()) {
            builder.addFilter(new ResponseLoggingFilter());
        }
        
        requestSpec = builder.build();
        
        System.out.println("=== Test Class Setup Complete: " + this.getClass().getSimpleName() + " ===");
    }

    /**
     * Setup before each test method.
     * Captures test name for logging and tracking.
     *
     * @param method The test method being executed
     */
    @BeforeMethod
    public void setUpMethod(Method method) {
        currentTestName = method.getName();
        System.out.println("\n>>> Starting Test: " + currentTestName + " <<<");
    }

    /**
     * Cleanup after each test method.
     * Cleans up any created test data.
     *
     * @param method The test method that was executed
     */
    @AfterMethod
    public void tearDownMethod(Method method) {
        System.out.println(">>> Finished Test: " + currentTestName + " <<<\n");
        
        // Cleanup created users if any
        cleanupCreatedUsers();
    }

    /**
     * Executes a GraphQL request and returns the response.
     *
     * @param requestBody The GraphQL request body
     * @return The response
     */
    protected Response executeGraphQLRequest(String requestBody) {
        return given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post()
                .then()
                .extract()
                .response();
    }

    /**
     * Executes a GraphQL request with custom logging.
     *
     * @param requestBody The GraphQL request body
     * @param logRequest  Whether to log the request
     * @param logResponse Whether to log the response
     * @return The response
     */
    protected Response executeGraphQLRequest(String requestBody, boolean logRequest, boolean logResponse) {
        var request = given()
                .spec(requestSpec)
                .body(requestBody);
        
        if (logRequest) {
            request = request.log().all();
        }
        
        var response = request
                .when()
                .post();
        
        if (logResponse) {
            response = response.then().log().all().extract().response();
        } else {
            response = response.then().extract().response();
        }
        
        return response;
    }

    /**
     * Validates that the response has a successful status code (200).
     *
     * @param response The response to validate
     */
    protected void assertSuccessfulResponse(Response response) {
        Assert.assertEquals(response.getStatusCode(), 200, 
            "Expected status code 200 but got " + response.getStatusCode());
    }

    /**
     * Validates that the GraphQL response doesn't contain errors.
     *
     * @param response The response to validate
     */
    protected void assertNoGraphQLErrors(Response response) {
        JsonPath jsonPath = new JsonPath(response.asString());
        Object errors = jsonPath.get("errors");
        Assert.assertNull(errors, "GraphQL response contains errors: " + errors);
    }

    /**
     * Validates both HTTP status and GraphQL errors.
     *
     * @param response The response to validate
     */
    protected void assertValidResponse(Response response) {
        assertSuccessfulResponse(response);
        assertNoGraphQLErrors(response);
    }

    /**
     * Extracts data from GraphQL response using JsonPath.
     *
     * @param response The response
     * @param path     The JsonPath expression
     * @param <T>      The expected return type
     * @return The extracted data
     */
    protected <T> T extractData(Response response, String path) {
        JsonPath jsonPath = new JsonPath(response.asString());
        return jsonPath.get(path);
    }

    /**
     * Tracks a created user ID for cleanup.
     *
     * @param userId The user ID to track
     */
    protected void trackCreatedUser(Integer userId) {
        if (userId != null) {
            createdUserIds.add(userId);
            System.out.println("Tracking user ID for cleanup: " + userId);
        }
    }

    /**
     * Cleans up all created users during the test.
     * This ensures test isolation and prevents data pollution.
     */
    protected void cleanupCreatedUsers() {
        if (createdUserIds.isEmpty()) {
            return;
        }
        
        System.out.println("Cleaning up " + createdUserIds.size() + " created user(s)...");
        
        for (Integer userId : createdUserIds) {
            try {
                deleteUser(userId);
                System.out.println("Deleted user ID: " + userId);
            } catch (Exception e) {
                System.err.println("Failed to delete user ID " + userId + ": " + e.getMessage());
            }
        }
        
        createdUserIds.clear();
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId The user ID to delete
     */
    protected void deleteUser(Integer userId) {
        base.GraphQLRequestBuilder requestBuilder = new base.GraphQLRequestBuilder()
                .mutationOperation("deleteUser")
                .argument("id", userId)
                .fields("id");
        
        given()
                .spec(requestSpec)
                .body(requestBuilder.build())
                .when()
                .post()
                .then()
                .statusCode(200);
    }

    /**
     * Gets the base URL from configuration.
     *
     * @return The base URL
     */
    protected String getBaseUrl() {
        return config.getBaseUrl();
    }

    /**
     * Gets the test user ID from configuration.
     *
     * @return The test user ID
     */
    protected int getTestUserId() {
        return config.getTestUserId();
    }

    /**
     * Gets the test username from configuration.
     *
     * @return The test username
     */
    protected String getTestUsername() {
        return config.getTestUsername();
    }

    /**
     * Gets the test password from configuration.
     *
     * @return The test password
     */
    protected String getTestPassword() {
        return config.getTestPassword();
    }

    /**
     * Gets the test auth token from configuration.
     *
     * @return The test auth token
     */
    protected String getTestAuthToken() {
        return config.getTestAuthToken();
    }

    /**
     * Waits for a specified duration (useful for async operations).
     *
     * @param milliseconds The duration to wait
     */
    protected void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Wait interrupted: " + e.getMessage());
        }
    }

    /**
     * Logs a message with test context.
     *
     * @param message The message to log
     */
    protected void log(String message) {
        System.out.println("[" + currentTestName + "] " + message);
    }
}

