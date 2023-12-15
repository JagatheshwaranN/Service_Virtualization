package qa.wiremock.concepts.server;

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
 * This code serves as a test suite for mocking an API endpoint using WireMock
 * and validating its behavior.
 * It dynamically starts a WireMock server, configures it to respond with specific
 * headers, a status code, and a body upon receiving a GET request to "/employee/1".
 * The test method within this class then simulates an API request to the specified
 * endpoint, leveraging RestAssured for HTTP interaction.
 *
 * @author Jagatheshwaran N
 */
public class StartServerFromCodeAndMockAPITest {

    // Constants for host and port
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    // Instance of WireMockServer
    public static WireMockServer wireMockServer;

    // Method to start the WireMock server and configure stubs before test execution
    @BeforeTest
    public void initializeServer() {
        // Create a new instance of WireMockServer
        wireMockServer = new WireMockServer(PORT);

        // Start the WireMockServer
        wireMockServer.start();

        // Configure WireMock to use the specified host and port
        WireMock.configureFor(HOST, PORT);

        // Create a response definition builder to define the response
        ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();

        // Set the response status code to 200
        responseDefinitionBuilder.withStatus(200);

        // Set response headers: Content-Type, Token, Set-Cookie
        responseDefinitionBuilder.withHeader("Content-Type", "text/json");
        responseDefinitionBuilder.withHeader("Token", "98765");
        responseDefinitionBuilder.withHeader("Set-Cookie", "session_id=987654321");

        // Set the response body content
        responseDefinitionBuilder.withBody("Hello John, Nice to see you!!");

        // Stub the WireMock server to respond with the defined response when receiving a GET request to "/employee/1"
        WireMock.stubFor(WireMock.get("/employee/1").willReturn(responseDefinitionBuilder));
    }

    // Method to shut down the WireMock server after test execution
    @AfterTest
    public void shutdownServer() {
        // Checks if the WireMock server is running and not null before shutting it down.
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    // Test method to verify the behavior of the mocked GET API
    @Test
    public void testStartServerFromCodeAndMockAPI() {
        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/employee/1", HOST, PORT);

        // Perform a GET request using RestAssured to the specified endpoint
        ValidatableResponse response =
                given() // Start building the request specification
                .when() // Define the HTTP method and endpoint
                        .get(requestUrl) // Specify the URL to send the GET request
                .then() // Begin handling the response
                        .assertThat() // Assert that the response has a status code of 200
                        .statusCode(200) // Validate the status code
                        .log() // Log the response details
                        .all(); // Log all details of the response

        // Assert that the extracted status line from the response matches "HTTP/1.1 200 OK"
        Assert.assertEquals(response.extract().statusLine(), "HTTP/1.1 200 OK");

        // Assert that the "Content-Type" header extracted from the response matches "text/json"
        Assert.assertEquals(response.extract().header("Content-Type"), "text/json");

        // Assert that the "Token" header extracted from the response matches "98765"
        Assert.assertEquals(response.extract().header("Token"), "98765");

        // Assert that the "Set-Cookie" header extracted from the response matches "session_id=987654321"
        Assert.assertEquals(response.extract().header("Set-Cookie"), "session_id=987654321");

        // Assert that the body content extracted from the response matches "Hello John, Nice to see you!!"
        Assert.assertEquals(response.extract().body().asString(), "Hello John, Nice to see you!!");
    }

}
