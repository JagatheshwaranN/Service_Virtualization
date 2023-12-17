package qa.wiremock.concepts.response_template;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.response.ValidatableResponse;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

/**
 * This code demonstrates how to use WireMock response templating to simulate
 * API responses with dynamic content based on request parameters, allowing for
 * efficient and reliable testing.
 * Leveraging WireMock's response templating capabilities, it effectively mimics
 * varying responses using predefined templates, enabling comprehensive validation
 * of client application behavior under different scenarios.
 * Response Templating: The emphasis on WireMock's response templating allows the
 * simulation of various responses based on predefined templates, enabling the
 * testing of how the service handles different data structures and scenarios.
 *
 * @author Jagatheshwaran N
 */
public class WireMockResponseTemplatingTest {

    // Constants for host and port
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    // Instance of WireMockServer
    public static WireMockServer wireMockServer;

    // Define the base file path for JSON files
    private static final String FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/__files/json/";

    // Method to start the WireMock server and configure stubs before test execution
    @BeforeTest
    public void startServer() {
        // Creates a new WireMock server instance.
        wireMockServer = new WireMockServer(PORT);

        // Starts the WireMock server.
        wireMockServer.start();

        // Configures WireMock to listen on the specified host and port.
        WireMock.configureFor(HOST, PORT);

        // Read the contents of the 'flight.json' file located in the specified FILE_PATH
        JSONObject flightJson = new JSONObject(readJsonFile(FILE_PATH + "flight.json"));

        // Extract the 'jsonBody' object from the 'response' object within the flightJson
        String responseBody = flightJson.getJSONObject("response").getJSONObject("jsonBody").toString(2);

        // Convert the extracted body to a UTF-8 string for proper representation
        responseBody = new String(responseBody.getBytes(StandardCharsets.UTF_8));

        // Stub the WireMock server to define a mocked response for the "/flights" GET request
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/flights"))
                // Set the response status code to 200 (OK)
                .willReturn(ResponseDefinitionBuilder.responseDefinition().withStatus(200)
                        // Include the extracted body as the response body
                        .withBody(responseBody)
                        // Apply the "response-template" transformer for further processing
                        .withTransformers("response-template")));

    }

    // Method to shut down the WireMock server after test execution
    @AfterTest
    public void shutdownServer() {
        // Checks if the WireMock server is running and not null before shutting it down.
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    // Test method to validate the behavior of the WireMockResponseTemplating by sending a GET request
    @Test
    public void testWireMockResponseTemplating() {
        // Construct the request URL using the HOST and PORT variables
        String requestUrl = String.format("http://%s:%d/flights?from=Chennai&to=Texas", HOST, PORT);

        // Perform the GET request and validate the response
        ValidatableResponse response =
                given() // Start building the request specification
                .when() // Perform the action (in this case, an HTTP GET request)
                        .get(requestUrl) // Specify the URL to send the GET request
                .then() // Start defining assertions on the response
                        .log().body(); // Log the response body for debugging purposes

        // Assert that the response status code is 200 (OK)
        Assert.assertEquals(response.extract().statusCode(), 200);
    }

    // Method to read the contents of a JSON file and return it as a string
    private String readJsonFile(String fileName) {
        // Convert the file name to a Path object
        Path path = Paths.get(fileName);

        try {
            // Read the contents of the file as a string using UTF-8 encoding
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // If an IOException occurs during file reading, wrap it in a RuntimeException and throw
            throw new RuntimeException(e);
        }
    }

}
