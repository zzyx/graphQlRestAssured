package base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class for building GraphQL request bodies.
 * Simplifies the creation of GraphQL queries with proper JSON formatting.
 * Supports both raw query strings and programmatic query construction.
 */
public class GraphQLRequestBuilder {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String query;
    private Map<String, Object> variables;
    private String operationName;

    // Fields for programmatic query building
    private OperationType operationType;
    private String operationFieldName;
    private Map<String, Object> arguments;
    private List<String> fields;
    private boolean isProgrammaticMode;

    private enum OperationType {
        QUERY, MUTATION
    }
    
    /**
     * Creates a new GraphQL request builder.
     */
    public GraphQLRequestBuilder() {
        this.variables = new HashMap<>();
        this.arguments = new LinkedHashMap<>();
        this.fields = new ArrayList<>();
        this.isProgrammaticMode = false;
    }
    
    /**
     * Sets the GraphQL query string.
     * Note: Using this method will override any programmatic query building.
     *
     * @param query The GraphQL query (can include newlines and formatting)
     * @return this builder for method chaining
     */
    public GraphQLRequestBuilder query(String query) {
        this.query = query;
        this.isProgrammaticMode = false;
        return this;
    }

    /**
     * Starts building a GraphQL query operation programmatically.
     *
     * @param fieldName The name of the query field (e.g., "getAllUsers", "getUser")
     * @return this builder for method chaining
     */
    public GraphQLRequestBuilder queryOperation(String fieldName) {
        this.operationType = OperationType.QUERY;
        this.operationFieldName = fieldName;
        this.isProgrammaticMode = true;
        return this;
    }

    /**
     * Starts building a GraphQL mutation operation programmatically.
     *
     * @param fieldName The name of the mutation field (e.g., "createUser", "updateUser")
     * @return this builder for method chaining
     */
    public GraphQLRequestBuilder mutationOperation(String fieldName) {
        this.operationType = OperationType.MUTATION;
        this.operationFieldName = fieldName;
        this.isProgrammaticMode = true;
        return this;
    }

    /**
     * Adds an argument to the operation.
     *
     * @param name The argument name
     * @param value The argument value
     * @return this builder for method chaining
     */
    public GraphQLRequestBuilder argument(String name, Object value) {
        this.arguments.put(name, value);
        return this;
    }

    /**
     * Adds multiple arguments to the operation.
     *
     * @param arguments Map of argument names to values
     * @return this builder for method chaining
     */
    public GraphQLRequestBuilder arguments(Map<String, Object> arguments) {
        if (arguments != null) {
            this.arguments.putAll(arguments);
        }
        return this;
    }

    /**
     * Adds fields to be returned in the response.
     *
     * @param fields Field names to include in the response
     * @return this builder for method chaining
     */
    public GraphQLRequestBuilder fields(String... fields) {
        this.fields.addAll(Arrays.asList(fields));
        return this;
    }

    /**
     * Adds fields to be returned in the response.
     *
     * @param fields List of field names to include in the response
     * @return this builder for method chaining
     */
    public GraphQLRequestBuilder fields(List<String> fields) {
        if (fields != null) {
            this.fields.addAll(fields);
        }
        return this;
    }
    
    /**
     * Adds a variable to the GraphQL request.
     * 
     * @param name The variable name
     * @param value The variable value
     * @return this builder for method chaining
     */
    public GraphQLRequestBuilder variable(String name, Object value) {
        this.variables.put(name, value);
        return this;
    }
    
    /**
     * Sets all variables at once.
     * 
     * @param variables Map of variable names to values
     * @return this builder for method chaining
     */
    public GraphQLRequestBuilder variables(Map<String, Object> variables) {
        this.variables = variables != null ? new HashMap<>(variables) : new HashMap<>();
        return this;
    }
    
    /**
     * Sets the operation name (optional).
     * 
     * @param operationName The name of the operation to execute
     * @return this builder for method chaining
     */
    public GraphQLRequestBuilder operationName(String operationName) {
        this.operationName = operationName;
        return this;
    }
    
    /**
     * Builds the GraphQL query string from programmatic inputs.
     *
     * @return The constructed GraphQL query string
     */
    private String buildProgrammaticQuery() {
        if (operationType == null || operationFieldName == null) {
            throw new IllegalStateException("Operation type and field name must be set");
        }

        StringBuilder queryBuilder = new StringBuilder();

        // Add operation type
        queryBuilder.append(operationType.name().toLowerCase()).append(" {\n");

        // Add operation field name
        queryBuilder.append("  ").append(operationFieldName);

        // Add arguments if present
        if (!arguments.isEmpty()) {
            queryBuilder.append("(");
            String argsString = arguments.entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + formatValue(entry.getValue()))
                    .collect(Collectors.joining(", "));
            queryBuilder.append(argsString);
            queryBuilder.append(")");
        }

        // Add fields only if specified (for object types)
        // Scalar types (String, Int, Boolean, etc.) don't have fields
        if (!fields.isEmpty()) {
            queryBuilder.append(" {\n");
            for (String field : fields) {
                queryBuilder.append("    ").append(field).append("\n");
            }
            queryBuilder.append("  }\n");
        } else {
            queryBuilder.append("\n");
        }

        queryBuilder.append("}");

        return queryBuilder.toString();
    }

    /**
     * Formats a value for GraphQL query syntax.
     *
     * @param value The value to format
     * @return Formatted string representation
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + value.toString().replace("\"", "\\\"") + "\"";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof List) {
            List<?> list = (List<?>) value;
            return "[" + list.stream()
                    .map(this::formatValue)
                    .collect(Collectors.joining(", ")) + "]";
        } else if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            return "{" + map.entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + formatValue(entry.getValue()))
                    .collect(Collectors.joining(", ")) + "}";
        } else {
            return "\"" + value.toString() + "\"";
        }
    }

    /**
     * Builds the GraphQL request body as a JSON string.
     *
     * @return JSON string representation of the GraphQL request
     * @throws RuntimeException if JSON serialization fails
     */
    public String build() {
        // Build query from programmatic inputs if in programmatic mode
        String finalQuery = isProgrammaticMode ? buildProgrammaticQuery() : query;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", finalQuery);

        if (variables != null && !variables.isEmpty()) {
            requestBody.put("variables", variables);
        } else {
            requestBody.put("variables", null);
        }

        if (operationName != null && !operationName.isEmpty()) {
            requestBody.put("operationName", operationName);
        }

        try {
            return objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to build GraphQL request body", e);
        }
    }
    
    /**
     * Builds the GraphQL request body as a Map (useful for RestAssured).
     *
     * @return Map representation of the GraphQL request
     */
    public Map<String, Object> buildAsMap() {
        // Build query from programmatic inputs if in programmatic mode
        String finalQuery = isProgrammaticMode ? buildProgrammaticQuery() : query;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", finalQuery);

        if (variables != null && !variables.isEmpty()) {
            requestBody.put("variables", variables);
        } else {
            requestBody.put("variables", null);
        }

        if (operationName != null && !operationName.isEmpty()) {
            requestBody.put("operationName", operationName);
        }

        return requestBody;
    }
    
    /**
     * Static factory method for creating a simple query without variables.
     * 
     * @param query The GraphQL query string
     * @return JSON string representation of the GraphQL request
     */
    public static String createSimpleQuery(String query) {
        return new GraphQLRequestBuilder()
                .query(query)
                .build();
    }
    
    /**
     * Static factory method for creating a query with variables.
     * 
     * @param query The GraphQL query string
     * @param variables Map of variable names to values
     * @return JSON string representation of the GraphQL request
     */
    public static String createQuery(String query, Map<String, Object> variables) {
        return new GraphQLRequestBuilder()
                .query(query)
                .variables(variables)
                .build();
    }
}

