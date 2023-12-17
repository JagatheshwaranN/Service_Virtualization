package qa.wiremock.concepts.response;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * This code demonstrates the usage of WireMock, a tool for simulating HTTP-based
 * services, to create stubs and mock responses for API endpoints.
 * Specifically, it illustrates the testing of response retrieval from a JSON file
 * using WireMock.
 * It ensures that the API responses match the expected structure and content defined
 * in the JSON file.
 * This code tests whether a GET request to the "/worker/1" endpoint retrieves the
 * expected data from the "json/worker.json" file through a mocked API response
 * using WireMock.
 *
 * @author Jagatheshwaran N
 */
public class ReadResponseFromJsonFileTest {

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

        // Creates a response definition builder object.
        ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();

        // Sets the response status code to 200 (OK).
        responseDefinitionBuilder.withStatus(200);

        // Sets the response content type to "application/json".
        responseDefinitionBuilder.withHeader("Content-Type", "application/json");

        // Sets the response header "Token" with the value "98765" to the response
        responseDefinitionBuilder.withHeader("Token", "98765");

        // Sets the "Set-Cookie" header with the session ID value "987654321" to set a session cookie
        responseDefinitionBuilder.withHeader("Set-Cookie", "session_id=987654321");

        // Specify the response body by reading the contents of the "json/worker.json" file
        responseDefinitionBuilder.withBodyFile("json/worker.json"); // Loads the expected response data from a JSON file

        // Stubbing a GET request for the '/worker/1' endpoint with the defined response
        WireMock.stubFor(WireMock.get("/worker/1").willReturn(responseDefinitionBuilder)); // Defines the request-response behavior
    }

    // Method to shut down the WireMock server after test execution
    @AfterTest
    public void shutdownServer() {
        // Checks if the WireMock server is running and not null before shutting it down.
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    // Test method to read and validate response from a JSON file using WireMock
    @Test
    public void testReadResponseFromJsonFile() {
        // Construct the request URL for the '/worker/1' endpoint using HOST and PORT variables
        String requestUrl = String.format("http://%s:%d/worker/1", HOST, PORT);

        // Send a GET request to the constructed URL and capture the response
        ValidatableResponse response =
                given() // Start building the request specification
                .when() // Perform the action (in this case, an HTTP GET request)
                        .get(requestUrl) // Specify the URL to send the GET request
                .then() // Start defining assertions on the response
                        .assertThat() // Use AssertJ for assertions
                        .statusCode(200) // Assert that the response status code is 200 (OK)
                        .log().all(); // Log all details of the response for debugging purposes

        // Validate various aspects of the response using assertions
        // Assert the response status line
        Assert.assertEquals(response.extract().statusLine(), "HTTP/1.1 200 OK");

        // Assert the Content-Type header
        Assert.assertEquals(response.extract().header("Content-Type"), "application/json");

        // Assert the Set-Cookie header
        Assert.assertEquals(response.extract().header("Set-Cookie"), "session_id=987654321");

        // Assert the 'id' field in JSON response
        Assert.assertEquals(response.extract().jsonPath().get("worker.id"), "EMP101");

        // Assert the 'name' field in JSON response
        Assert.assertEquals(response.extract().jsonPath().get("worker.name"), "John Doe");

        // Assert the 'country' field in JSON response
        Assert.assertEquals(response.extract().jsonPath().get("worker.address.country"), "United States");
    }

}
