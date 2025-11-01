import base.BaseGraphQLTest;
import base.GraphQLRequestBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for GraphQL PATCH operations with GET verification.
 * Demonstrates a complete update workflow: GET -> UPDATE -> GET -> VERIFY
 */
public class TestPatchCallWithGet extends BaseGraphQLTest {

    @Test
    public void testPatch() {
        int userId = getTestUserId();
        String newName = "John McKenzie";

        // Step 1: Get user details before patch
        log("Step 1: Getting user details before update");
        String getUserRequest = new GraphQLRequestBuilder()
                .queryOperation("getAllUsers")
                .fields("id", "firstName", "lastName", "email", "gender", "ipaddress")
                .build();

        Response responseBeforePatch = executeGraphQLRequest(getUserRequest);
        assertValidResponse(responseBeforePatch);

        JsonPath pathBefore = new JsonPath(responseBeforePatch.asString());
        String firstNameBefore = pathBefore.get("data.getAllUsers.find { it.id == " + userId + " }.firstName");
        log("First name before patch: " + firstNameBefore);

        // Step 2: Make Patch
        log("Step 2: Updating user with ID: " + userId);
        String patchRequest = new GraphQLRequestBuilder()
                .mutationOperation("updateUser")
                .argument("id", userId)
                .argument("firstName", newName)
                .fields("id", "firstName", "lastName", "email", "gender", "ipaddress")
                .build();

        Response patchResponse = executeGraphQLRequest(patchRequest);
        assertValidResponse(patchResponse);
        log("Patch completed successfully");

        // Step 3: Get user details after patch and verify update
        log("Step 3: Verifying user was updated");
        Response responseAfterPatch = executeGraphQLRequest(getUserRequest);
        assertValidResponse(responseAfterPatch);

        JsonPath pathAfter = new JsonPath(responseAfterPatch.asString());
        String firstNameAfter = pathAfter.get("data.getAllUsers.find { it.id == " + userId + " }.firstName");
        log("First name after patch: " + firstNameAfter);

        // Verify that the firstName was updated
        Assert.assertEquals(firstNameAfter, newName, "First name should be updated to " + newName);
        Assert.assertNotEquals(firstNameAfter, firstNameBefore, "First name should have changed");

        log("Update verification successful!");
    }
}
