package qa.wiremock.concepts;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class MockGetAPITest {

    // Constants for host and port
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    // Instance of WireMockServer
    public static WireMockServer wireMockServer;

    // Method to start WireMockServer before tests
    @BeforeTest
    public void startupServer() {
        // Create and start the WireMock server
        wireMockServer = new WireMockServer(PORT);

        try {
            // Start the WireMockServer
            wireMockServer.start();

            // Configure WireMock for host and port
            WireMock.configureFor(HOST, PORT);

            // Define the expected response for the GET /user/emp101 endpoint
            ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();

            // Set the expected status code
            responseDefinitionBuilder.withStatus(200);

            // Set the expected Content-Type header
            responseDefinitionBuilder.withHeader("Content-Type", "application/json");

            // Set the response body file
            responseDefinitionBuilder.withBodyFile("json/get_user.json");

            // Stub the GET request for a specific URL with the prepared response
            WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/user/emp101")).willReturn(responseDefinitionBuilder));
        } catch (Exception e) {
            // Handle any startup exceptions
            e.printStackTrace();
        }
    }

    // Method to shut down WireMockServer after tests
    @AfterTest
    public void shutdownServer() {
        // Stop the WireMock server if it is running
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    // Test method to mock the GET API call and validate the response
    @Test
    public void testMockGetAPI() {
        // Construct the request URL using the HOST and PORT constants
        String requestUrl = String.format("http://%s:%d/user/emp101", HOST, PORT);

        // Perform the GET request and validate the response
        ValidatableResponse response =
                given()
                .when()
                        .get(requestUrl)
                .then()
                        .assertThat()
                        .statusCode(200) // Check for status code 200
                        .log()
                        .all();

        // Extract and verify specific response data
        // Verify the status code
        Assert.assertEquals(response.extract().statusCode(), 200, "Unexpected status code");
        // Verify the Content-Type header
        Assert.assertEquals(response.extract().header("Content-Type"), "application/json", "Unexpected Content-Type");
        // Verify the worker ID
        Assert.assertEquals(response.extract().jsonPath().get("worker.id"), "EMP101", "Incorrect worker ID");
    }

}

