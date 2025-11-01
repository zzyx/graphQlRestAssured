import base.BaseGraphQLTest;
import base.GraphQLRequestBuilder;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for GraphQL SignUp mutation.
 */
public class TestSignUp extends BaseGraphQLTest {

    @Test
    public void signUpTest() {
        log("Testing sign-up with username and password");

        String requestBody = new GraphQLRequestBuilder()
                .mutationOperation("signUp")
                .argument("username", getTestUsername())
                .argument("password", getTestPassword())
                .fields("username", "authToken")
                .build();

        log("Executing sign-up mutation");
        Response response = executeGraphQLRequest(requestBody);

        // Validate response
        assertValidResponse(response);

        // Extract and validate sign-up data
        String username = extractData(response, "data.signUp.username");
        String authToken = extractData(response, "data.signUp.authToken");

        Assert.assertNotNull(username, "Username should not be null");
        Assert.assertNotNull(authToken, "Auth token should not be null");
        Assert.assertEquals(username, getTestUsername(), "Username should match");
        Assert.assertFalse(authToken.isEmpty(), "Auth token should not be empty");

        log("Sign-up successful for user: " + username);
        log("Auth token received: " + authToken.substring(0, Math.min(20, authToken.length())) + "...");
    }
}
