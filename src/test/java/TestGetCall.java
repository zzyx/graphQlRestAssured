import base.BaseGraphQLTest;
import base.GraphQLRequestBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests for GraphQL GET/Query operations.
 */
public class TestGetCall extends BaseGraphQLTest {

    @Test
    public void testGetCall() {
        log("Building GraphQL query to get all users");
        String requestBody = new GraphQLRequestBuilder()
                .queryOperation("getAllUsers")
                .fields("firstName", "id")
                .build();

        log("Executing GraphQL request");
        Response response = executeGraphQLRequest(requestBody);

        // Validate response
        assertValidResponse(response);

        // Extract and validate data
        JsonPath path = new JsonPath(response.asString());
        Object firstName = path.get("data.getAllUsers.firstName");

        // Verify that we got some users back
        Assert.assertNotNull(firstName, "First name list should not be null");
        log("Successfully retrieved user data");
    }

    /**
     * Validates that multiple specific first names are present in the response.
     * This demonstrates how to validate multiple fields in the response.
     */
    @Test
    public void validateMultipleContent() {
        log("Building GraphQL query to validate multiple users");
        String requestBody = new GraphQLRequestBuilder()
                .queryOperation("getAllUsers")
                .fields("firstName", "id")
                .build();

        log("Executing GraphQL request with content validation");
        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post()
                .then()
                .assertThat()
                .body("data.getAllUsers.firstName", hasItems("Wilbur", "Oriana", "Brade", "Sebastian"))
                .statusCode(200);

        log("Successfully validated multiple user names in response");
    }
}
