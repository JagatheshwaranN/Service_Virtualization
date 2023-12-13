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
 * The code focuses on simulating and testing the behavior of a POST API endpoint
 * ("/user/add") by mocking its response.
 * It sets up a WireMock server, defines a stub for the specified API endpoint,
 * sends a request to this mocked endpoint, and verifies whether the response matches
 * the expected behavior.
 * It also ensures that only one POST request was made to the URL "/user/add"
 * within the scope of the test.
 * This helps to confirm that the test is behaving as intended and that there
 * are no unexpected interactions with the mocked API.
 *
 * @author Jagatheshwaran N
 */
public class VerifyMockPostAPITest {

    // Constants for host and port
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    // Instance of WireMockServer
    public static WireMockServer wireMockServer;

    @BeforeTest
    public void startupServer() {
        // Initialize and start WireMockServer
        wireMockServer = new WireMockServer(PORT);

        // Start the WireMockServer
        wireMockServer.start();

        // Configure WireMock for host and port
        WireMock.configureFor(HOST, PORT);

        // Define the expected response for the POST /user/add endpoint
        ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();

        // Set the expected status code
        responseDefinitionBuilder.withStatus(201);

        // Set the expected Content-Type header
        responseDefinitionBuilder.withHeader("Content-Type", "application/json");

        // Set the response body file
        responseDefinitionBuilder.withBodyFile("json/add_user.json");

        // Stub the WireMock behavior for the POST request with expected payload
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/user/add"))
                .withRequestBody(WireMock.equalToJson(getPayload())) // Verify the request body matches the expected payload
                .willReturn(responseDefinitionBuilder));

    }

    // Method to shut down WireMockServer after tests
    @AfterTest
    public void shutdownServer() {
        // Check if WireMockServer is initialized and running, then shut it down
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    @Test
    public void testVerifyMockPostAPI() {
        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/user/add", HOST, PORT);

        // Get the JSON payload for the request
        String payloadJson = getPayload();

        // Perform the POST request and validate the response
        ValidatableResponse response =
                given() // Start building the request specification
                        .body(payloadJson) // Set the request body to the provided JSON payload
                .when() // Perform the action (in this case, an HTTP POST request)
                        .post(requestUrl) // Specify the URL to send the POST request
                .then() // Start defining assertions on the response
                        .assertThat() // Begin assertion configuration
                        .statusCode(201) // Check that the response status code is 201 (Created)
                        .log() // Log details of the request and response
                        .all(); // Log all details (request headers, body, response headers, and body)

        // Verifying that the POST request was made to the specified endpoint once and
        // received the expected request once.
        WireMock.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo("/user/add")).withRequestBody(WireMock.equalToJson(
                getPayload())));

        // Assertions to validate specific fields in the response
        // Verify the status code
        Assert.assertEquals(201, response.extract().statusCode(), "Unexpected status code");

        // Verify the Content-Type header
        Assert.assertEquals(response.extract().header("Content-Type"),"application/json", "Unexpected Content-Type");

        // Verify the worker ID
        Assert.assertEquals(response.extract().jsonPath().get("worker.id"), "EMP101", "Incorrect worker ID");

        // Verify the worker name
        Assert.assertEquals(response.extract().jsonPath().get("worker.name"), "John Doe", "Incorrect worker name");

        // Verifies additional JSON values using JSON Path assertions.
        Assert.assertEquals(response.extract().jsonPath().get("worker.address.city"), "New York");
        Assert.assertEquals(response.extract().jsonPath().get("worker.address.country"), "United States");
        Assert.assertEquals(response.extract().jsonPath().get("worker.createdAt"), "2023-11-04T02:48:52.454Z");
    }

    // Method to return the JSON payload for the POST request
    private String getPayload() {
        return """
                {
                  "name": "John Doe",
                  "location": "New York",
                  "phone": "123-456-7890",
                  "address": {
                    "city": "New York",
                    "state": "New York",
                    "zipcode": "10001",
                    "country": "United States"
                  }
                }""";
    }

}
