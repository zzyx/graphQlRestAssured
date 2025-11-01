package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.fe;

/**
 * Centralized configuration management for GraphQL API tests.
 * Supports both properties file and environment variable overrides.
 * Environment variables take precedence over properties file values.
 */
public class TestConfig {

    private static final String CONFIG_FILE = "test.properties";
    private static final Properties properties = new Properties();
    private static TestConfig instance;

    // Configuration keys
    private static final String BASE_URL_KEY = "graphql.base.url";
    private static final String CONTENT_TYPE_KEY = "graphql.content.type";
    private static final String CONNECTION_TIMEOUT_KEY = "graphql.connection.timeout";
    private static final String REQUEST_TIMEOUT_KEY = "graphql.request.timeout";
    private static final String TEST_USER_ID_KEY = "test.user.id";
    private static final String TEST_USERNAME_KEY = "test.username";
    private static final String TEST_PASSWORD_KEY = "test.password";
    private static final String TEST_AUTH_TOKEN_KEY = "test.auth.token";
    private static final String ENABLE_REQUEST_LOGGING_KEY = "test.enable.request.logging";
    private static final String ENABLE_RESPONSE_LOGGING_KEY = "test.enable.response.logging";

    // Default values
    private static final String DEFAULT_BASE_URL = "https://graphql-api-ppql.onrender.com/graphql";
    private static final String DEFAULT_CONTENT_TYPE = "application/json";
    private static final String DEFAULT_CONNECTION_TIMEOUT = "10000";
    private static final String DEFAULT_REQUEST_TIMEOUT = "30000";
    private static final String DEFAULT_TEST_USER_ID = "21";
    private static final String DEFAULT_USERNAME = "AdminUser1";
    private static final String DEFAULT_PASSWORD = "Admin@User1";
    private static final String DEFAULT_AUTH_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6IkFkbWluVXNlcjEiLCJpYXQiOjE2OTI4NjgwMTh9.rfHb6DlO_fB8DKxxjtRSAqFvwtWSHHZDMqNMunmqfpM";
    private static final String DEFAULT_ENABLE_REQUEST_LOGGING = "false";
    private static final String DEFAULT_ENABLE_RESPONSE_LOGGING = "true";

    private TestConfig() {
        loadProperties();
    }

    /**
     * Gets the singleton instance of TestConfig.
     *
     * @return TestConfig instance
     */
    public static synchronized TestConfig getInstance() {
        if (instance == null) {
            instance = new TestConfig();
        }
        return instance;
    }

    /**
     * Loads properties from the configuration file.
     * If the file doesn't exist, default values will be used.
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                System.out.println("Loaded configuration from " + CONFIG_FILE);
            } else {
                System.out.println("Configuration file " + CONFIG_FILE + " not found. Using default values.");
            }
        } catch (IOException e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
            System.out.println("Using default configuration values.");
        }
    }

    /**
     * Gets a configuration value with the following priority:
     * 1. Environment variable (if set)
     * 2. System property (if set)
     * 3. Properties file value (if exists)
     * 4. Default value
     *
     * @param key          The configuration key
     * @param defaultValue The default value if not found
     * @return The configuration value
     */
    private String getConfigValue(String key, String defaultValue) {
        // First check environment variable (convert dots to underscores and uppercase)
        String envKey = key.replace('.', '_').toUpperCase();
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }

        // Then check system property
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isEmpty()) {
            return sysProp;
        }

        // Then check properties file
        String propValue = properties.getProperty(key);
        if (propValue != null && !propValue.isEmpty()) {
            return propValue;
        }

        // Finally return default
        return defaultValue;
    }

    /**
     * Gets the GraphQL API base URL.
     * Can be overridden with environment variable: GRAPHQL_BASE_URL
     *
     * @return The base URL
     */
    public String getBaseUrl() {
        return getConfigValue(BASE_URL_KEY, DEFAULT_BASE_URL);
    }

    /**
     * Gets the content type for requests.
     * Can be overridden with environment variable: GRAPHQL_CONTENT_TYPE
     *
     * @return The content type
     */
    public String getContentType() {
        return getConfigValue(CONTENT_TYPE_KEY, DEFAULT_CONTENT_TYPE);
    }

    /**
     * Gets the connection timeout in milliseconds.
     * Can be overridden with environment variable: GRAPHQL_CONNECTION_TIMEOUT
     *
     * @return The connection timeout
     */
    public int getConnectionTimeout() {
        return Integer.parseInt(getConfigValue(CONNECTION_TIMEOUT_KEY, DEFAULT_CONNECTION_TIMEOUT));
    }

    /**
     * Gets the request timeout in milliseconds.
     * Can be overridden with environment variable: GRAPHQL_REQUEST_TIMEOUT
     *
     * @return The request timeout
     */
    public int getRequestTimeout() {
        return Integer.parseInt(getConfigValue(REQUEST_TIMEOUT_KEY, DEFAULT_REQUEST_TIMEOUT));
    }

    /**
     * Gets the test user ID for update/delete operations.
     * Can be overridden with environment variable: TEST_USER_ID
     *
     * @return The test user ID
     */
    public int getTestUserId() {
        return Integer.parseInt(getConfigValue(TEST_USER_ID_KEY, DEFAULT_TEST_USER_ID));
    }

    /**
     * Gets the test username for authentication.
     * Can be overridden with environment variable: TEST_USERNAME
     *
     * @return The test username
     */
    public String getTestUsername() {
        return getConfigValue(TEST_USERNAME_KEY, DEFAULT_USERNAME);
    }

    /**
     * Gets the test password for authentication.
     * Can be overridden with environment variable: TEST_PASSWORD
     *
     * @return The test password
     */
    public String getTestPassword() {
        return getConfigValue(TEST_PASSWORD_KEY, DEFAULT_PASSWORD);
    }

    /**
     * Gets the test auth token for authentication.
     * Can be overridden with environment variable: TEST_AUTH_TOKEN
     *
     * @return The test auth token
     */
    public String getTestAuthToken() {
        return getConfigValue(TEST_AUTH_TOKEN_KEY, DEFAULT_AUTH_TOKEN);
    }

    /**
     * Checks if request logging is enabled.
     * Can be overridden with environment variable: TEST_ENABLE_REQUEST_LOGGING
     *
     * @return true if request logging is enabled
     */
    public boolean isRequestLoggingEnabled() {
        return Boolean.parseBoolean(getConfigValue(ENABLE_REQUEST_LOGGING_KEY, DEFAULT_ENABLE_REQUEST_LOGGING));
    }

    /**
     * Checks if response logging is enabled.
     * Can be overridden with environment variable: TEST_ENABLE_RESPONSE_LOGGING
     *
     * @return true if response logging is enabled
     */
    public boolean isResponseLoggingEnabled() {
        return Boolean.parseBoolean(getConfigValue(ENABLE_RESPONSE_LOGGING_KEY, DEFAULT_ENABLE_RESPONSE_LOGGING));
    }

    /**
     * Prints all current configuration values (useful for debugging).
     */
    public void printConfiguration() {
        System.out.println("=== Test Configuration ===");
        System.out.println("Base URL: " + getBaseUrl());
        System.out.println("Content Type: " + getContentType());
        System.out.println("Connection Timeout: " + getConnectionTimeout() + "ms");
        System.out.println("Request Timeout: " + getRequestTimeout() + "ms");
        System.out.println("Test User ID: " + getTestUserId());
        System.out.println("Test Username: " + getTestUsername());
        System.out.println("Request Logging: " + isRequestLoggingEnabled());
        System.out.println("Response Logging: " + isResponseLoggingEnabled());
        System.out.println("========================");
    }
}

