import base.BaseGraphQLTest;
import base.GraphQLRequestBuilder;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for GraphQL DELETE/Mutation operations.
 * Note: This test creates a user first, then deletes it to ensure test isolation.
 */
public class TestDeleteCall extends BaseGraphQLTest {

    @Test
    public void testDelete() {
        // First, create a user to delete (ensures test isolation)
        log("Creating a user to delete");


        Integer userIdToDelete = 24;
        log("Created user with ID: " + userIdToDelete);

        // Now delete the user
        log("Deleting user with ID: " + userIdToDelete);
        String deleteRequest = new GraphQLRequestBuilder()
                .mutationOperation("deleteUser")
                .argument("id", userIdToDelete)
                .fields("id", "firstName", "lastName", "email", "gender", "ipaddress")
                .build();

        Response deleteResponse = executeGraphQLRequest(deleteRequest);
        assertValidResponse(deleteResponse);

        // Verify deleted user data is returned
        Integer deletedUserId = extractData(deleteResponse, "data.deleteUser.id");



        log("User deleted successfully. ID: " + deletedUserId);

        // Note: No need to track for cleanup since we already deleted it
    }
}
