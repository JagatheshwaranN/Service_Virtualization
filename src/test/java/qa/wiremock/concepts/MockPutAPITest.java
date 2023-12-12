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

public class MockPutAPITest {

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

            // Define the expected response for the PUT /user/update/.* endpoint
            ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();

            // Set the expected status code
            responseDefinitionBuilder.withStatus(200);

            // Set the expected Content-Type header
            responseDefinitionBuilder.withHeader("Content-Type", "application/json");

            // Set the response body file
            responseDefinitionBuilder.withBodyFile("json/update_user.json");

            // Stub the WireMock behavior for the PUT request with expected payload
            WireMock.stubFor(WireMock.put(WireMock.urlMatching("/user/update/.*"))
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

    // Test method to mock the PUT API call and validate the response
    @Test
    public void testMockPutAPI() {
        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/user/update/emp101", HOST, PORT);

        // Get the JSON payload for the request
        String payloadJson = getPayload();

        // Perform the PUT request and validate the response
        ValidatableResponse response =
                given() // Start building the request specification
                        .body(payloadJson) // Set the request body to the provided JSON payload
                .when() // Perform the action (in this case, an HTTP POST request)
                        .put(requestUrl) // Specify the URL to send the PUT request
                .then() // Start defining assertions on the response
                        .assertThat() // Begin assertion configuration
                        .statusCode(200) // Check that the response status code is 200 (Created)
                        .log() // Log details of the request and response
                        .all(); // Log all details (request headers, body, response headers, and body)

        // Assertions to validate specific fields in the response
        // Verify the status code
        Assert.assertEquals(200, response.extract().statusCode(), "Unexpected status code");

        // Verify the Content-Type header
        Assert.assertEquals(response.extract().header("Content-Type"),"application/json", "Unexpected Content-Type");

        // Verify the worker ID
        Assert.assertEquals(response.extract().jsonPath().get("worker.id"), "EMP101", "Incorrect worker ID");

        // Verify the worker name
        Assert.assertEquals(response.extract().jsonPath().get("worker.name"), "John Doe", "Incorrect worker name");

        // Verify the worker city
        Assert.assertEquals(response.extract().jsonPath().get("worker.address.city"), "MiddleTown");
    }

    // Method to return the JSON payload for the PUT request
    private String getPayload() {
        return """
                {
                  "name": "John Doe",
                  "location": "New York",
                  "phone": "123-456-7890",
                  "address": {
                    "city": "MiddleTown",
                    "state": "New York",
                    "zipcode": "10001",
                    "country": "United States"
                  }
                }""";
    }

}
