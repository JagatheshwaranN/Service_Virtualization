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

public class MockPostAPITest {

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

    // Test method to mock the POST API call and validate the response
    @Test
    public void testMockPostAPI() {
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

        // Assertions to validate specific fields in the response
        // Verify the status code
        Assert.assertEquals(201, response.extract().statusCode(), "Unexpected status code");

        // Verify the Content-Type header
        Assert.assertEquals(response.extract().header("Content-Type"),"application/json", "Unexpected Content-Type");

        // Verify the worker ID
        Assert.assertEquals(response.extract().jsonPath().get("worker.id"), "EMP101", "Incorrect worker ID");

        // Verify the worker name
        Assert.assertEquals(response.extract().jsonPath().get("worker.name"), "John Doe", "Incorrect worker name");
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


