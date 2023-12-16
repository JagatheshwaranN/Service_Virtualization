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
 * This code utilizes WireMock to mimic a specific fault scenario involving the
 * generation of random data followed by the closure of the connection.
 * It sets up a WireMock server and configures it to listen on a specific port (8080).
 * It defines a stub for any HTTP method sent to the /user/emp103 URL path.
 * This stub uses the Fault.RANDOM_DATA_THEN_CLOSE fault, instructing WireMock to
 * Generate a response with randomly generated data of an unspecified length.
 * Abruptly close the connection after sending the first chunk of data.
 * The test method sends a GET request to the /user/emp103 endpoint using Apache HttpClient.
 *
 * @author Jagatheshwaran N
 */
public class RandomDataExceptionTest {

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

        // Stub any request with the URL path "/user/emp103"
        // to generate a fault: Randomly generated data response
        WireMock.stubFor(
                // Matches any HTTP method for the specified URL
                WireMock.any(WireMock.urlPathEqualTo("/user/emp103"))
                        .willReturn(WireMock.aResponse()
                                // Simulating a fault: Randomly generated data, then the connection is closed
                                .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));
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

    // Test method to verify the behavior of the fault: Randomly generated data response exception scenario
    @Test
    public void testRandomDataException() {
        // Construct the request URL using the HOST and PORT constants for '/user/emp103'
        String requestUrl = String.format("http://%s:%d/user/emp103", HOST, PORT);

        // Assert that executing the request throws a NoHttpResponseException
        Assert.assertThrows(org.apache.hc.core5.http.NoHttpResponseException.class, () -> {

            // Execute the GET request to the constructed URL
            org.apache.hc.client5.http.fluent.Response response = Request.get(requestUrl).execute();

            // Print the response body (if available)
            System.out.println("Response Body :" + response.returnContent());
        });
    }

}
