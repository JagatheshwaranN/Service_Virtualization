package qa.wiremock.concepts.proxy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class WireMockProxyTest {

    // Constants for host and port
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    // Instance of WireMockServer
    public static WireMockServer wireMockServer;

    // Method to start the WireMock server and configure stubs before test execution
    @BeforeTest
    public void startupServer() {
        // Create a new instance of WireMockServer
        wireMockServer = new WireMockServer(PORT);

        // Configure WireMock to listen on the specified host and port
        WireMock.configureFor(HOST, PORT);

        // Start the WireMock server
        wireMockServer.start();

        // Stub any request URL to be proxied from "http://localhost:3000/students/2"
        WireMock.stubFor(
                WireMock.any(WireMock.anyUrl()) // Match any HTTP method and any URL
                        .willReturn(WireMock.aResponse()
                                        .proxiedFrom("http://localhost:3000/students/2"))); // Proxy the response from the specified URL

    }

    // Method to shut down the WireMock server after test execution
    @AfterTest
    public void shutdownServer() {
        // Check if the WireMock server instance exists and is running
        if (wireMockServer != null && wireMockServer.isRunning()) {

            // Shutdown the WireMock server
            wireMockServer.shutdownServer();
        }
    }

    // Test method to verify the behavior of the proxied GET API
    @Test
    public void testWireMockProxy() {
        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d", HOST, PORT);

        // Send a GET request to "http://localhost:8080"
        ValidatableResponse response =
                given() // Start building the request specification
                .when() // Perform the action (in this case, an HTTP GET request)
                    .get(requestUrl) // Specify the URL to send the GET request
                .then() // Start defining assertions on the response
                    .statusCode(200) // Check that the response status code is 200 (OK)
                    .body("id", equalTo(2)); // Validate that the response body contains an 'id' with a value of 2

        // Print the response details in a pretty format
        System.out.println(response.extract().asPrettyString());

        // Assert that the 'id' in the response body matches the expected value of 2
        Assert.assertEquals(response.extract().body().jsonPath().getInt("id"), 2);
    }

}
