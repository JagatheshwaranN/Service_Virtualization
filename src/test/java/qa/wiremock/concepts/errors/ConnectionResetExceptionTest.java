package qa.wiremock.concepts.errors;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.apache.hc.client5.http.fluent.Request;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * This code demonstrates the fault injection using WireMock to simulate a
 * "connection reset by peer" scenario.
 * It sets up a WireMock server and configures it, and also defines a stub
 * for any HTTP method sent to the /user/emp103 URL path.
 * The stub is configured to return a response with the Fault.CONNECTION_RESET_BY_PEER
 * fault, which simulates the server abruptly terminating the connection.
 * The test method then makes a GET request to the /user/emp103 endpoint using
 * the Apache HttpClient library.
 *
 * @author Jagatheshwaran N
 */
public class ConnectionResetExceptionTest {

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

        // Stubbing any request with the URL path '/user/emp103' to simulate
        // a connection reset by peer fault
        WireMock.stubFor(
                // Matching any HTTP method for the specific URL path
                WireMock.any(WireMock.urlPathEqualTo("/user/emp103"))
                        .willReturn(WireMock.aResponse()
                                // Simulating a connection reset by peer fault
                                .withFault(Fault.CONNECTION_RESET_BY_PEER)));
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

    // Test method to validate the occurrence of a SocketException (connection reset) when making a GET request
    @Test
    public void testConnectionResetException() {
        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/user/emp103", HOST, PORT);

        // Assert that a ConnectionResetException is thrown when trying to access a non-existent endpoint.
        Assert.assertThrows(java.net.SocketException.class, () -> {

            // Executes a GET request to the constructed URL
            org.apache.hc.client5.http.fluent.Response response = Request.get(requestUrl).execute();

            // Outputs the response body if the request succeeds (won't be reached in case of exception)
            System.out.println("Response Body :" + response.returnContent());
        });
    }

}
