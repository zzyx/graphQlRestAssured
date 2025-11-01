import base.BaseGraphQLTest;
import base.GraphQLRequestBuilder;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for GraphQL PATCH/Mutation operations (Update User).
 */
public class TestPatchCall extends BaseGraphQLTest {

    @Test
    public void testPatch() {
        int userId = getTestUserId();
        String newFirstName = "Denver";

        log("Building GraphQL mutation to update user ID: " + userId);
        String requestBody = new GraphQLRequestBuilder()
                .mutationOperation("updateUser")
                .argument("id", userId)
                .argument("firstName", newFirstName)
                .fields("id", "firstName", "lastName", "email", "gender", "ipaddress")
                .build();

        log("Executing update user mutation");
        Response response = executeGraphQLRequest(requestBody);

        // Validate response
        assertValidResponse(response);

        // Extract and validate updated data
        String updatedFirstName = extractData(response, "data.updateUser.firstName");
        Integer updatedUserId = extractData(response, "data.updateUser.id");

        Assert.assertEquals(updatedUserId, userId, "User ID should match");
        Assert.assertEquals(updatedFirstName, newFirstName, "First name should be updated to " + newFirstName);

        log("User updated successfully. New first name: " + updatedFirstName);
    }
}
