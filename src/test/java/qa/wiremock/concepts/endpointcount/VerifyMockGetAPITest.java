package qa.wiremock.concepts.endpointcount;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * The code aims to simulate and test the behavior of a GET API endpoint
 * ("/user/emp101") by mocking its response.
 * It sets up a WireMock server and defines a stub for the specified API
 * endpoint.
 * Subsequently, it sends a request to this mocked endpoint and verifies
 * whether the response matches the expected behavior.
 * It also ensures that only one GET request was made to the URL "/user/emp101"
 * within the scope of the test.
 * This helps to confirm that the test is behaving as intended and that there
 * are no unexpected interactions with the mocked API.
 *
 * @author Jagatheshwaran N
 */
public class VerifyMockGetAPITest {

    // Constants for host and port
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    // Instance of WireMockServer
    public static WireMockServer wireMockServer;

    // Method to start the WireMock server and configure stubs before test execution
    @BeforeTest
    public void startupServer() {
        // Creates a new WireMock server instance.
        wireMockServer = new WireMockServer(PORT);

        // Starts the WireMock server.
        wireMockServer.start();

        // Configures WireMock to listen on the specified host and port.
        WireMock.configureFor(HOST, PORT);

        // Creates a response definition builder object.
        ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();

        // Sets the response status code to 200 (OK).
        responseDefinitionBuilder.withStatus(200);

        // Sets the response content type to "application/json".
        responseDefinitionBuilder.withHeader("Content-Type", "application/json");

        // Defines the response body using a JSON file located in "json/get_user.json".
        responseDefinitionBuilder.withBodyFile("json/get_user.json");

        // Stubs a response for the GET request to "/user/emp101".
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/user/emp101"))
                .willReturn(responseDefinitionBuilder));
    }

    // Method to shut down the WireMock server after test execution
    @AfterTest
    public void shutdownServer() {
        // Checks if the WireMock server is running and not null before shutting it down.
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    // Test method to verify the behavior of the mocked GET API
    @Test
    public void testVerifyMockGetAPI() {
        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/user/emp101", HOST, PORT);

        // Perform the GET request and validate the response
        ValidatableResponse response =
                given() // Start building the request specification
                .when() // Perform the action (in this case, an HTTP GET request)
                        .get(requestUrl) // Specify the URL to send the GET request
                .then() // Start defining assertions on the response
                        .assertThat() // Begin assertion configuration
                        .statusCode(200) // Check that the response status code is 200 (OK)
                        .log() // Log details of the request and response
                        .all(); // Log all details (request headers, body, response headers, and body)

        // Verifying that the GET request was made to the specified endpoint once and
        // received the expected request once.
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/user/emp101")));

        // Assertions to validate specific fields in the response
        // Verify the status code
        Assert.assertEquals(response.extract().statusCode(), 200, "Unexpected status code");

        // Verify the Content-Type header
        Assert.assertEquals(response.extract().header("Content-Type"), "application/json", "Unexpected Content-Type");

        // Verify the worker ID
        Assert.assertEquals(response.extract().jsonPath().get("worker.id"), "EMP101", "Incorrect worker ID");

        // Verifies additional JSON values using JSON Path assertions.
        Assert.assertEquals(response.extract().jsonPath().get("worker.name"), "John Doe");
        Assert.assertEquals(response.extract().jsonPath().get("worker.address.city"), "New York");
        Assert.assertEquals(response.extract().jsonPath().get("worker.address.country"), "United States");
        Assert.assertEquals(response.extract().jsonPath().get("worker.retrievedAt"), "2023-11-04T03:48:52.454Z");
    }

}
