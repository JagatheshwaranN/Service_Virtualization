package qa.wiremock.concepts.recordandplay;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * This code illustrates the use of WireMock, a versatile tool for stubbing and
 * recording HTTP interactions in testing environments.
 * The class showcases the process of setting up a WireMock server, configuring
 * stubs, recording API interactions, and validating the behavior of a recorded API.
 *
 * @author Jagatheshwaran N
 */
public class WireMockRecordAPITest {

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

        // Start recording API interactions for the specified endpoint
        wireMockServer.startRecording("http://localhost:3000/students/1");
    }

    // Method to shut down the WireMock server after test execution
    @AfterTest
    public void shutdownServer() {
        // Check if the WireMock server instance exists and is running
        if (wireMockServer != null && wireMockServer.isRunning()) {
            // Stop the recording on the WireMock server
            wireMockServer.stopRecording();

            // Stop the WireMock server
            wireMockServer.stop();

            // Ensure proper resource cleanup
            wireMockServer = null;
        }
    }

    // Test method to verify the behavior of the recorded GET API
    @Test
    public void testWireMockRecordAPI() {
        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d", HOST, PORT);

        given() // Start building the request specification
        .when() // Perform the action (in this case, an HTTP GET request)
                .get(requestUrl) // Specify the URL to send the GET request
        .then() // Start defining assertions on the response
                .statusCode(200) // Check that the response status code is 200 (OK)
                .body("id", equalTo(1))  // Validate that the response body contains an 'id' field with the value of 1
                .log() // Log details of the request and response
                .all(); // Log all details (request headers, body, response headers, and body)
    }

}

/*
        Reference for future actions
        ============================
        // WireMock.saveAllMappings();
        // StubMapping[] stubMappings = wireMockServer.getStubMappings().toArray(new StubMapping[0]);
        // StubMapping stubMapping = wireMockServer.getStubMappings().get(0);
        // System.out.println(stubMapping);
*/
