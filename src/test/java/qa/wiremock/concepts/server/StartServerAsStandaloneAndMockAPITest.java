package qa.wiremock.concepts.server;

import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * This code demonstrates the use of RestAssured to interact with a WireMock
 * standalone mock server, aiming to simulate API responses.
 * The test suite leverages RestAssured's functionalities to verify the behavior
 * of a WireMock API server set up on the local host at port 8080.
 * The tests focus on different endpoints ("/user/1" and "/user/2"), checking status
 * codes and content types to ensure the expected responses are being simulated.
 *
 * @author Jagatheshwaran N
 */
public class StartServerAsStandaloneAndMockAPITest {

    // Constants for host and port
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    // Test method to verify the behavior of the mocked GET API
    @Test(priority = 1)
    public void testStartServerAsStandaloneAndMockAPIType1() {
        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/user/1", HOST, PORT);

        // Perform a GET request using RestAssured to the specified endpoint
        given() // Start building the request specification
        .when() // Define the HTTP method and endpoint
                .get(requestUrl) // Specify the URL to send the GET request
        .then() // Begin handling the response
                .assertThat() // Assert that the response has a status code of 200
                .statusCode(200) // Validate the status code
                .log() // Log the response details
                .all(); // Log all details of the response
    }

    // Test method to verify the behavior of the mocked GET API
    @Test(priority = 2)
    public void testStartServerAsStandaloneAndMockAPIType2() {
        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/user/2", HOST, PORT);

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

        // Extract the "Content-Type" header from the response
        String contentType = response.extract().header("Content-Type");

        // Print the content type of the response
        System.out.println("Response Content Type : " + contentType);

        // Assert that the extracted content type matches "text/plain"
        Assert.assertEquals(contentType, "text/plain");
    }

}
