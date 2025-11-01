import base.BaseGraphQLTest;
import base.GraphQLRequestBuilder;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for GraphQL SignIn mutation.
 */
public class TestSignIn extends BaseGraphQLTest {

    @Test
    public void signInTest() {
        log("Testing sign-in with username and auth token");

        // signIn mutation returns a String (token), not an object, so no fields are needed
        String requestBody = new GraphQLRequestBuilder()
                .mutationOperation("signIn")
                .argument("username", getTestUsername())
                .argument("authToken", getTestAuthToken())
                .build();

        log("Executing sign-in mutation");
        Response response = executeGraphQLRequest(requestBody, true, true);

        // Validate response
        assertValidResponse(response);

        // Verify we got a token back
        String token = extractData(response, "data.signIn");
        Assert.assertNotNull(token, "Sign-in should return a token");
        Assert.assertFalse(token.isEmpty(), "Token should not be empty");

        log("Sign-in successful. Token received: " + token.substring(0, Math.min(20, token.length())) + "...");
    }
}
