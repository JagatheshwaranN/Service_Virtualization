package qa.wiremock.concepts.recordandplay;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WireMockPlaybackAPITest {

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
    }

    // Method to shut down the WireMock server after test execution
    @AfterTest
    public void shutdownServer() {
        // Checks if the WireMock server is running and not null before shutting it down.
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    // Test method to verify the behavior of the play-back GET API
    @Test
    public void testWireMockPlaybackAPI() throws IOException {
        // Create a new instance of WireMock
        WireMock wireMock = new WireMock();

        // Construct the request URL using the specified host and port
        String requestUrl = String.format("http://%s:%d", HOST, PORT);

        // Read the content of the pre-recorded JSON mapping file into a string
        String jsonString = Files.readString(Paths.get("src/test/resources/mappings/get--4880e29c-6507-4719-894c-d8e43c7268e3.json"), StandardCharsets.UTF_8);

        // Build a StubMapping object from the JSON string representation
        StubMapping stubMapping = StubMapping.buildFrom(jsonString);

        // Register the stub mapping with WireMock
        wireMock.register(stubMapping);

        // Send a GET request to the playback server using RestAssured
        Response response = RestAssured.get(requestUrl);

        // Print the response body in a formatted way
        response.prettyPrint();

        // Assert that the response status code is 200 (OK)
        Assert.assertEquals(response.getStatusCode(), (200));
    }

}
