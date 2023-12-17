package qa.wiremock.concepts.url;

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
 * This test ensures utilizes WireMock, a powerful tool for mocking HTTP-based services.
 * Wiremock can appropriately handle any incoming request with any URL pattern by
 * configuring a generic response that matches all URLs, and validates the response
 * received from a simulated GET request against predefined assertions.
 * This allows for testing scenarios where multiple URL patterns need to be stubbed
 * generically.
 *
 * @author Jagatheshwaran N
 */
public class WireMockAnyUrlGetAPITest {

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

        // Specify the response body by reading the contents of the "json/get_user.json" file
        responseDefinitionBuilder.withBodyFile("json/get_user.json");

        // Create a stub for any incoming request (using WireMock's any() method)
        // and specify that any URL pattern will match
        WireMock.stubFor(
                // Match any HTTP method on any URL
                WireMock.any(WireMock.anyUrl())
                        // Define the response to be returned for any request matching the criteria
                        .willReturn(responseDefinitionBuilder)
        );
    }

    // Method to shut down the WireMock server after test execution
    @AfterTest
    public void shutdownServer() {
        // Checks if the WireMock server is running and not null before shutting it down.
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    @Test
    public void testWireMockAnyUrlGetAPI() {

        // Construct the request URL for the '/user/emp101' endpoint using HOST and PORT variables
        String requestUrl = String.format("http://%s:%d/user/emp101", HOST, PORT);

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
        // Assert the response status code
        Assert.assertEquals(response.extract().statusCode(), 200);

        // Assert the Content-Type header in the response
        Assert.assertEquals(response.extract().header("Content-Type"), "application/json");

        // Assert the 'worker ID' field in JSON response
        Assert.assertEquals(response.extract().jsonPath().get("worker.id"), "EMP101");

        // Assert the 'worker name' field in JSON response
        Assert.assertEquals(response.extract().jsonPath().get("worker.name"), "John Doe");

        // Assert the 'city' field in JSON response
        Assert.assertEquals(response.extract().jsonPath().get("worker.address.city"), "New York");

        // Assert the 'country' field in JSON response
        Assert.assertEquals(response.extract().jsonPath().get("worker.address.country"), "United States");

        // Assert the 'retrieved timestamp' field in JSON response
        Assert.assertEquals(response.extract().jsonPath().get("worker.retrievedAt"), "2023-11-04T03:48:52.454Z");
    }

}
