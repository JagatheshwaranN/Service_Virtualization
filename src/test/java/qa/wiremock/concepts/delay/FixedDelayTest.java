package qa.wiremock.concepts.delay;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class FixedDelayTest {

    // Constants for host and port
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    // Instance of WireMockServer
    public static WireMockServer wireMockServer;

    // Method to set up the WireMock server before tests
    @BeforeTest
    public void setupServer() {
        // Creates a new WireMock server instance.
        wireMockServer = new WireMockServer(PORT);

        // Starts the WireMock server.
        wireMockServer.start();

        // Configures WireMock to listen on the specified host and port.
        WireMock.configureFor(HOST, PORT);

        // Stubbing any URL to simulate a fixed delay of 5000ms with a proxy response
        WireMock.stubFor(WireMock.any(WireMock.anyUrl()) // Match any request on any URL
                .willReturn(WireMock.aResponse() // Define response
                        .proxiedFrom("http://localhost:3000/students/2") // Proxy response from another service
                        .withFixedDelay(5000))); // Simulate a delay of 5 seconds
    }

    // Method to shut down the WireMock server after tests
    @AfterTest
    public void shutdownServer() {
        // Check if the server is running and then shut it down
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    // Test to verify a simulated fixed delay for a specific endpoint
    @Test
    public void shouldSimulateFixedDelayForEndpoint() {
        // Define the expected Id
        int expectedId = 2;

        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d", HOST, PORT);

        // Perform the GET request and validate the response
        ValidatableResponse response =
                given() // Start building the request specification
                .when() // Perform the action (in this case, an HTTP GET request)
                        .get(requestUrl) // Specify the URL to send the GET request
                .then() // Start defining assertions on the response
                        .statusCode(200) // Check that the response status code is 200 (OK)
                        .body("id", equalTo(2)); // Verify the response body's 'id' is 2

        // Log the response as pretty printed
        System.out.println(response.extract().asPrettyString());

        // Assert that the 'id' in the response body matches the expected value 2
        Assert.assertEquals(response.extract().body().jsonPath().getInt("id"), expectedId, "Unexpected ID in the response");
    }

}
