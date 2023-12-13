package qa.wiremock.concepts.delay;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class LogNormalRandomDelayTest {

    // Constants for host and port
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    // Instance of WireMockServer
    public static WireMockServer wireMockServer;

    // Method to set up the WireMock server before tests
    @BeforeTest
    public void startupServer() {
        // Creates a new WireMock server instance.
        wireMockServer = new WireMockServer(PORT);

        // Starts the WireMock server.
        wireMockServer.start();

        // Configures WireMock to listen on the specified host and port.
        WireMock.configureFor(HOST, PORT);

        // Create a response definition builder for the endpoint
        ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();

        // Use a pre-defined JSON file for consistent response content
        responseDefinitionBuilder.withBodyFile("json/randelay.json");

        // Stub the endpoint to simulate a log-normal random delay with specified mean and standard deviation
        WireMock.stubFor(WireMock.get("/random/delay")
                // Define the response with a log-normal random delay
                .willReturn(responseDefinitionBuilder.withLogNormalRandomDelay(1000, 0.1)));
    }

    // Method to shut down the WireMock server after tests
    @AfterTest
    public void shutdownServer() {
        // Check if the server is running and then shut it down
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    // Test to verify a simulated log-normal random delay for a specific endpoint
    @Test
    public void testLogNormalRandomDelaySimulation() {
        // Define the expected status code
        int expectedStatusCode = 200;

        // Record the start time to calculate response time
        long startTime = System.currentTimeMillis();

        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/random/delay", HOST, PORT);

        // Perform the GET request and validate the response
        ValidatableResponse response =
                given() // Start building the request specification
                        .when() // Perform the action (in this case, an HTTP GET request)
                        .get(requestUrl) // Specify the URL to send the GET request
                        .then() // Start defining assertions on the response
                        .statusCode(200); // Check that the response status code is 200 (OK)

        // Record the end time after receiving the response
        long endTime = System.currentTimeMillis();

        // Calculate the elapsed time (response time)
        long elapsedTime = endTime - startTime;

        // Print response delay time
        System.out.println("Response delay time : " + elapsedTime + "ms");

        // Assert that the response delay time is within the expected range
        Assert.assertTrue(elapsedTime >= 1000 && elapsedTime <= 4000,
                "Unexpected delay time in response");

        // Verify the status code should be 200 (Success)
        Assert.assertEquals(response.extract().statusCode(), expectedStatusCode, "Unexpected status code");

        // Print response details for debugging or visibility purposes
        System.out.println(response.extract().asPrettyString());
    }

}
