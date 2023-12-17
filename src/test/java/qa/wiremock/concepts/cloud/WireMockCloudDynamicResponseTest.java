package qa.wiremock.concepts.cloud;

import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * This code exemplifies a test designed to validate the behavior of a WireMock cloud service.
 * This test ensures that when requesting flight information from Chennai to California via
 * the specified WireMock Cloud service's API endpoint, the response received should have a
 * status code of 200 (indicating success).
 * It specifically tests the retrieval of flight information by simulating a GET request with
 * specific query parameters.
 * It's a fundamental test validating the functionality and responsiveness of the WireMock
 * cloud service.
 *
 * @author Jagatheshwaran N
 */
public class WireMockCloudDynamicResponseTest {

    // Test method to validate WireMock cloud dynamic response
    @Test
    public void testWireMockCloudDynamicResponse() {
        // Build the GET request URL for the flights API on WireMock Cloud
        String requestUrl = "https://testautomation.wiremockapi.cloud/flights";

        // Perform a GET request to a WireMock cloud service with query parameters 'from' and 'to'
        ValidatableResponse response =
                given() // Start building the request specification
                        .queryParam("from", "Chennai") // Set query parameter for "from" location (Chennai)
                        .queryParam("to", "California") // Set query parameter for "to" location (California)
                .when() // Perform the action (in this case, an HTTP GET request)
                        .get(requestUrl) // Specify the URL to send the GET request
                .then() // Start defining assertions on the response
                        .log().body(); // Log the response body for debugging purposes

        // Assert that the response status code is 200 (OK)
        Assert.assertEquals(response.extract().statusCode(), 200);
    }
}
