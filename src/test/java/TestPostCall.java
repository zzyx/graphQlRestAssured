import base.BaseGraphQLTest;
import base.GraphQLRequestBuilder;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.TestDataGenerator;
import utils.TestDataGenerator.UserData;

/**
 * Tests for GraphQL POST/Mutation operations (Create User).
 */
public class TestPostCall extends BaseGraphQLTest {

    @Test
    public void testPost() {
        // Generate unique test data for isolation
        UserData userData = TestDataGenerator.generateUserData("CreateTest");

        log("Building GraphQL mutation to create a new user: " + userData.getFirstName());
        String requestBody = new GraphQLRequestBuilder()
                .mutationOperation("createUser")
                .argument("firstName", userData.getFirstName())
                .argument("lastName", userData.getLastName())
                .argument("gender", userData.getGender())
                .argument("ipaddress", userData.getIpAddress())
                .argument("email", userData.getEmail())
                .fields("id", "firstName", "lastName", "email", "gender", "ipaddress")
                .build();

        log("Executing create user mutation");
        Response response = executeGraphQLRequest(requestBody);

        // Validate response
        assertValidResponse(response);

        // Extract created user data
        Integer createdUserId = extractData(response, "data.createUser.id");
        String firstName = extractData(response, "data.createUser.firstName");
        String lastName = extractData(response, "data.createUser.lastName");
        String email = extractData(response, "data.createUser.email");

        // Validate created data
        Assert.assertEquals(firstName, userData.getFirstName(), "First name should match");
        Assert.assertEquals(lastName, userData.getLastName(), "Last name should match");
        Assert.assertEquals(email, userData.getEmail(), "Email should match");

        log("User created successfully with ID: " + createdUserId);

        // Track for cleanup
        trackCreatedUser(createdUserId);
    }
}
