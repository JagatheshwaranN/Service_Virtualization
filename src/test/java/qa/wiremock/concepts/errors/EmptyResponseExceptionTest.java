package qa.wiremock.concepts.errors;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.apache.hc.client5.http.fluent.Request;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class EmptyResponseExceptionTest {

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

        // Stubbing any request URL that matches "/user/emp103" and returning an empty response
        WireMock.stubFor(
                // Match any HTTP method and the specific URL path
                WireMock.any(WireMock.urlPathEqualTo("/user/emp103"))
                        .willReturn(WireMock.aResponse()
                                // Generate a response with an empty body
                                .withFault(Fault.EMPTY_RESPONSE)));
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

    // Test method to verify the behavior of the empty response exception scenario
    @Test
    public void testEmptyResponseException() {
        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/user/emp103", HOST, PORT);

        // Asserting that an org.apache.hc.core5.http.NoHttpResponseException is thrown
        Assert.assertThrows(org.apache.hc.core5.http.NoHttpResponseException.class, () -> {

            // Sending a GET request to "http://localhost:8080/user/emp103" using HTTP Client Fluent API
            org.apache.hc.client5.http.fluent.Response response = Request.get(requestUrl).execute();

            // Printing the response body if the request is successful (Note: This line will not execute if an exception is thrown)
            System.out.println("Response Body :" + response.returnContent());
        });
    }

}
