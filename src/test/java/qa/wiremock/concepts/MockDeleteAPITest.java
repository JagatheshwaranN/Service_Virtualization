package qa.wiremock.concepts;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class MockDeleteAPITest {

    // Constants for host and port
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    // Instance of WireMockServer
    public static WireMockServer wireMockServer;

    // Method to start WireMockServer before tests
    @BeforeTest
    public void startupServer() {
        // Initialize and start WireMockServer
        wireMockServer = new WireMockServer(PORT);

        try {
            // Start the WireMockServer
            wireMockServer.start();

            // Configure WireMock for host and port
            WireMock.configureFor(HOST, PORT);

            // Define the expected response for the DELETE /user/.* endpoint
            ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();

            // Set the expected status code
            responseDefinitionBuilder.withStatus(204);

            // Set the expected Content-Type header
            responseDefinitionBuilder.withHeader("Content-Type", "application/json");

            // Set the response body file
            responseDefinitionBuilder.withBodyFile("json/delete_user.json");

            // Stub the WireMock behavior for the DELETE request
            WireMock.stubFor(WireMock.delete(WireMock.urlPathMatching("/user/.*"))
                    .willReturn(responseDefinitionBuilder));
        } catch (Exception e) {
            // Handle any startup exceptions
            e.printStackTrace();
        }
    }

    // Method to shut down WireMockServer after tests
    @AfterTest
    public void shutdownServer() {
        // Check if WireMockServer is initialized and running, then shut it down
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    // Test method to mock the DELETE API call and validate the response
    @Test
    public void testMockDeleteAPI() {
        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/user/emp101", HOST, PORT);

        // Perform the DELETE request and validate the response
        ValidatableResponse response =
                given() // Start building the request specification
                        .when() // Perform the action (in this case, an HTTP DELETE request)
                        .get(requestUrl) // Specify the URL to send the DELETE request
                        .then() // Start defining assertions on the response
                        .assertThat() // Begin assertion configuration
                        .statusCode(200) // Check that the response status code is 200 (No Content)
                        .log() // Log details of the request and response
                        .all(); // Log all details (request headers, body, response headers, and body)

        // Assertions to validate specific fields in the response
        // Verify the status code
        Assert.assertEquals(response.extract().statusCode(), 204, "Unexpected status code");

        // Verify the Content-Type header
        Assert.assertEquals( response.extract().header("Content-Type"), "application/json","Unexpected Content-Type");
    }

}
