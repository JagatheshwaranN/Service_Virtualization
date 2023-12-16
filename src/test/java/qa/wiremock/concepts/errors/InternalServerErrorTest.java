package qa.wiremock.concepts.errors;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class InternalServerErrorTest {

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

        // Stubbing any request with the URL path "/user/emp102" to return
        // a server error (HTTP status code 500)
        WireMock.stubFor(
                // Match any HTTP method for the URL path "/user/emp102"
                WireMock.any(WireMock.urlPathEqualTo("/user/emp102"))
                        // Define the response to return a server error (status code 500)
                        .willReturn(WireMock.serverError()));
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

    // Test method to verify the behavior of the internal server error scenario
    @Test
    public void testInternalServerError() {
        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/user/emp102", HOST, PORT);

        // Send a GET request to the specified URL
        Response response = RestAssured.get(requestUrl);

        // Print the HTTP status code received in the response
        System.out.println(response.getStatusCode());

        // Print the status line received in the response
        System.out.println(response.getStatusLine());

        // Assert that the response status code is 500 (internal server error)
        Assert.assertEquals(response.getStatusCode(), 500);
    }

}
