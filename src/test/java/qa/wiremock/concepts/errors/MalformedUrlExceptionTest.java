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
 * This code utilizes WireMock, a mock server, to simulate a specific fault scenario
 * and test how a system behaves when faced with a malformed response chunk.
 * It sets up a WireMock server and configures it to listen on a specific port (8080).
 * It defines a stub for any HTTP method sent to the /user/emp103 URL path.
 * This stub uses the Fault.MALFORMED_RESPONSE_CHUNK fault, instructing WireMock to
 * inject a malformed chunk into the response body, simulating an invalid data structure.
 * The test method sends a GET request to the /user/emp103 endpoint using Apache HttpClient.
 *
 * @author Jagatheshwaran N
 */
public class MalformedUrlExceptionTest {

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

        // Stubbing any request to a specific URL path ("/user/emp103")
        // to generate a Fault with a malformed response chunk
        WireMock.stubFor(
                // Matches any HTTP method for the specified URL
                WireMock.any(WireMock.urlPathEqualTo("/user/emp103"))
                        .willReturn(WireMock.aResponse()
                                // Simulating a Fault with a malformed response
                                .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
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

    // Test method to verify the behavior of the Fault with a malformed response exception scenario
    @Test
    public void testMalformedUrlException() {
        // Construct the request URL using the HOST and PORT constants for '/user/emp103'
        String requestUrl = String.format("http://%s:%d/user/emp103", HOST, PORT);

        // Assert that executing the GET request to the constructed URL throws a MalformedChunkCodingException
        Assert.assertThrows(org.apache.hc.core5.http.MalformedChunkCodingException.class, () -> {

            // Execute the GET request to the constructed URL
            org.apache.hc.client5.http.fluent.Response response = Request.get(requestUrl).execute();

            // Print the response body (if available)
            System.out.println("Response Body :" + response.returnContent());
        });
    }

}
