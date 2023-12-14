package qa.wiremock.concepts.conditionalmock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;

/**
 * This code showcases the utilization of WireMock for conditional mocking of
 * HTTP GET requests to different endpoints based on specific header values.
 * It sets up a WireMock server and configures stubs to respond differently
 * based on the Accept header in the request.
 * It helps simulate diverse responses (such as service availability and
 * specific status) and response times for different content types, allowing
 * developers and testers to verify the system's behavior under various
 * request conditions.
 *
 * @author Jagatheshwaran N
 */
public class ConditionalMockingTest {

    // Constants for host and port
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    // Instance of WireMockServer
    public static WireMockServer wireMockServer;

    // Method to start the WireMock server and configure stubs before test execution
    @BeforeTest
    public void initializeServer() {
        // Creates a new WireMock server instance.
        wireMockServer = new WireMockServer(PORT);

        // Starts the WireMock server.
        wireMockServer.start();

        // Configures WireMock to listen on the specified host and port.
        WireMock.configureFor(HOST, PORT);

        // Creates a response definition builder object
        ResponseDefinitionBuilder serviceUnavailableResponse = new ResponseDefinitionBuilder();

        // Set response status code to 503 (Service Unavailable)
        serviceUnavailableResponse.withStatus(503);

        // Set Content-Type header to text/html
        serviceUnavailableResponse.withHeader("Content-Type", "text/html");

        // Set response body to "Service Not Available"
        serviceUnavailableResponse.withBody("Service Not Available");

        // Stubs a response for the GET request to '/movies/1' with Accept header for plain text
        WireMock.stubFor(WireMock.get("/movies/1")
                .withHeader("Accept", WireMock.equalTo("text/plain")) // Match request based on GET method and Accept header
                .willReturn(serviceUnavailableResponse)); // Define response for matched request

        // Creates a response definition builder object
        ResponseDefinitionBuilder runningResponse = new ResponseDefinitionBuilder();

        // Set response status code to 200 (OK)
        runningResponse.withStatus(200);

        // Set Content-Type header to application/json
        runningResponse.withHeader("Content-Type", "application/json");

        // Set response body to '"current-status": "running"'
        runningResponse.withBody("""
                {
                  "current-status": "running"
                }""");

        // Set response body and delay response by 2.5 seconds
        runningResponse.withFixedDelay(2500);

        // Stubs a response for the GET request to '/movies/1' with Accept header for application/json
        WireMock.stubFor(WireMock.get("/movies/1")
                .withHeader("Accept", WireMock.equalTo("application/json")) // Match request based on GET method and Accept header
                .willReturn(runningResponse)); // Define response for matched request
    }

    // Method to shut down the WireMock server after test execution
    @AfterTest
    public void shutdownServer() {
        // Checks if the WireMock server is running and not null before shutting it down
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    // Test method to mock the GET API call and validate the response
    @Test(priority = 1)
    public void testConditionalMockingOfGetAPIWithPlainTextHeader() {
        // Define the expected response
        String expectedResponse = "Service Not Available";

        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/movies/1", HOST, PORT);

        // Perform the GET request and validate the response
        ValidatableResponse response =
                given() // Start building the request specification
                        .header("Accept", "text/plain") // Set the request header to accept text/html
                .when(). // Perform the action (in this case, an HTTP GET request)
                        get(requestUrl) // Specify the URL to send the GET request
                .then() // Start defining assertions on the response
                        .assertThat() // Begin assertion configuration
                        .statusCode(503) // Check that the response status code is 503 (Service Unavailable)
                        .log() // Log details of the request and response
                        .all(); // Log all details (request headers, body, response headers, and body)

        // Verify response body contains expected response
        Assert.assertTrue(response.extract().body().asString().contains(expectedResponse));
    }

    // Test method to mock the GET API call and validate the response
    @Test(priority = 2)
    public void testConditionalMockingOfGetAPIWithApplicationJsonHeader() {
        // Define the expected response
        String expectedResponse = "running";

        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/movies/1", HOST, PORT);

        ValidatableResponse response =
                given() // Start building the request specification
                        .header("Accept", "application/json") // Set the request header to accept JSON
                .when() // Perform the action (in this case, an HTTP GET request)
                        .get(requestUrl) // Specify the URL to send the GET request
                .then() // Start defining assertions on the response
                        .assertThat() // Begin assertion configuration
                        .statusCode(200) // Check that the response status code is 200 (OK)
                        .log() // Log details of the request and response
                        .all(); // Log all details (request headers, body, response headers, and body)

        // Verify response body contains expected response
        Assert.assertEquals(response.extract().body().jsonPath().get("current-status"), expectedResponse);
    }

}
