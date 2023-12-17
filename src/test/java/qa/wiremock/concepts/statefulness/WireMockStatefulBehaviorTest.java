package qa.wiremock.concepts.statefulness;

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

public class WireMockStatefulBehaviorTest {

    // Constants for host and port
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    // Instance of WireMockServer
    public static WireMockServer wireMockServer;

    // Define the base file path for JSON files
    private static final String FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/__files/json/cart/";

    // Instance of scenarioName to store a scenario's name
    private String scenarioName;

    // Instance of scenarioState to store the state of a scenario
    private String scenarioState;

    // Method to start the WireMock server and configure stubs before test execution
    @BeforeTest
    public void startServer() {
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
        // Check if the WireMock server instance exists and is running
        if (wireMockServer != null && wireMockServer.isRunning()) {

            // Shutdown the WireMock server
            wireMockServer.shutdownServer();
        }
    }

    // Test method to simulate an empty cart scenario
    @Test(priority = 1)
    public void emptyCart() {
        // Construct the request URL for cart items using the HOST and PORT variables
        String requestUrl = String.format("http://%s:%d/cart-items", HOST, PORT);

        // Read the content of 'emptycart.json' containing the scenario details and parse it as a JSON object
        JSONObject emptyCartJson = new JSONObject(readJsonFile(FILE_PATH + "emptycart.json"));

        // Extract scenario details: scenarioName and requiredScenarioState from the JSON object
        scenarioName = emptyCartJson.getString("scenarioName");
        scenarioState = emptyCartJson.getString("requiredScenarioState");

        // Extract the response body from the "response" object within the JSON for an empty cart scenario
        String responseBody = emptyCartJson.getJSONObject("response").toString(2);

        // Convert the response body to a UTF-8 string for proper representation
        responseBody = new String(responseBody.getBytes(StandardCharsets.UTF_8));

        // Stub the WireMock server for a GET request to '/cart-items' for the empty cart scenario
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/cart-items"))
                // Specify the scenario name and required state for scenario-based mocking
                .inScenario(scenarioName)
                .whenScenarioStateIs(scenarioState)
                // Define the response with status code 200 and the extracted response body
                .willReturn(ResponseDefinitionBuilder.responseDefinition().withStatus(200)
                        .withBody(responseBody)));

        // Send a GET request to the constructed URL and capture the response for validation
        ValidatableResponse response =
                given() // Start building the request specification
                .when() // Perform the action (in this case, an HTTP GET request)
                        .get(requestUrl) // Specify the URL to send the GET request
                .then() // Start defining assertions on the response
                        .log().body(); // Log the response body for debugging purposes

        // Assert that the response status code is 200 (OK)
        Assert.assertEquals(response.extract().statusCode(), 200);
    }

    // Test method to simulate adding an item to the cart
    @Test(priority = 2)
    public void addCart() {
        // Construct the request URL for cart items using the HOST and PORT variables
        String requestUrl = String.format("http://%s:%d/cart-items", HOST, PORT);

        // Read the content of 'addcart.json' containing the scenario details and parse it as a JSON object
        JSONObject addCartJson = new JSONObject(readJsonFile(FILE_PATH + "addcart.json"));

        // Extract scenario details: scenarioName and requiredScenarioState from the JSON object
        scenarioName = addCartJson.getString("scenarioName");
        scenarioState = addCartJson.getString("requiredScenarioState");

        // Extract the response body from the "response" object within the JSON for the add to cart scenario
        String responseBody = addCartJson.getJSONObject("response").toString(2);

        // Convert the response body to a UTF-8 string for proper representation
        responseBody = new String(responseBody.getBytes(StandardCharsets.UTF_8));

        // Stub the WireMock server for a POST request to '/cart-items' to add an item to the cart
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/cart-items"))
                // Specify the scenario name and required state for scenario-based mocking
                .inScenario(scenarioName)
                .whenScenarioStateIs(scenarioState)
                // Verify the request body contains the expected pattern from the 'addcart.json'
                .withRequestBody(WireMock.containing(addCartJson.getJSONObject("request")
                        .getJSONArray("bodyPatterns").getJSONObject(0).getString("contains")))
                // Set the response status code to 201 (Created) and provide the extracted body
                .willReturn(ResponseDefinitionBuilder.responseDefinition().withStatus(201)
                        .withBody(responseBody))
                // Set the state to a new scenario state after the action
                .willSetStateTo(addCartJson.getString("newScenarioState")));

        // Send a POST request with a body of "MicroService Architecture" to the constructed URL
        // (This represents adding an item to the cart)
        ValidatableResponse response =
                given() // Start building the request specification
                        .body("MicroService Architecture") // Set the request body to the provided String
                .when() // Perform the action (in this case, an HTTP POST request)
                        .post(requestUrl) // Specify the URL to send the POST request
                .then() // Start defining assertions on the response
                        .log().body(); // Log the response body for debugging purposes

        // Assert that the response status code is 201 (Created)
        Assert.assertEquals(response.extract().statusCode(), 201);
    }

    // Test method to simulate a full cart scenario
    @Test(priority = 3)
    public void fullCart() {
        // Construct the request URL for cart items using the HOST and PORT variables
        String requestUrl = String.format("http://%s:%d/cart-items", HOST, PORT);

        // Read the content of 'fullcart.json' containing the scenario details and parse it as a JSON object
        JSONObject fullCartJson = new JSONObject(readJsonFile(FILE_PATH + "fullcart.json"));

        // Extract scenario details: scenarioName and requiredScenarioState from the JSON object
        scenarioName = fullCartJson.getString("scenarioName");
        scenarioState = fullCartJson.getString("requiredScenarioState");

        // Extract the response body from the "response" object within the JSON for the full cart scenario
        String responseBody = fullCartJson.getJSONObject("response").toString(2);

        // Convert the response body to a UTF-8 string for proper representation
        responseBody = new String(responseBody.getBytes(StandardCharsets.UTF_8));

        // Stub the WireMock server for a GET request to '/cart-items' for the full cart scenario
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/cart-items"))
                // Specify the scenario name and required state for scenario-based mocking
                .inScenario(scenarioName)
                .whenScenarioStateIs(scenarioState)
                // Define the response with status code 200 and the extracted response body
                .willReturn(ResponseDefinitionBuilder.responseDefinition().withStatus(200)
                        .withBody(responseBody)));

        // Send a GET request to the constructed URL and capture the response for validation
        ValidatableResponse response =
                given() // Start building the request specification
                .when() // Perform the action (in this case, an HTTP GET request)
                        .get(requestUrl) // Specify the URL to send the GET request
                .then() // Start defining assertions on the response
                        .log().body(); // Log the response body for debugging purposes

        // Assert that the response status code is 200 (OK)
        Assert.assertEquals(response.extract().statusCode(), 200);

        // Assert that the response body contains the string "MicroService Architecture"
        Assert.assertTrue(response.extract().body().asString().contains("MicroService Architecture"));
    }

    // Test method to simulate deleting items from the cart
    @Test(priority = 4)
    public void deleteCart() {
        // Construct the request URL for cart items using the HOST and PORT variables
        String requestUrl = String.format("http://%s:%d/cart-items", HOST, PORT);

        // Read the content of 'deletecart.json' containing the scenario details and parse it as a JSON object
        JSONObject deleteCartJson = new JSONObject(readJsonFile(FILE_PATH + "deletecart.json"));

        // Extract scenario details: scenarioName and requiredScenarioState from the JSON object
        scenarioName = deleteCartJson.getString("scenarioName");
        scenarioState = deleteCartJson.getString("requiredScenarioState");

        // Stub the WireMock server for a DELETE request to '/cart-items' to delete items from the cart
        WireMock.stubFor(WireMock.delete(WireMock.urlPathEqualTo("/cart-items"))
                // Specify the scenario name and required state for scenario-based mocking
                .inScenario(scenarioName)
                .whenScenarioStateIs(scenarioState)
                // Set the response status code to 204 (No Content) for successful deletion
                .willReturn(ResponseDefinitionBuilder.responseDefinition().withStatus(204))
                // Set the state to a new scenario state after the action
                .willSetStateTo(deleteCartJson.getString("newScenarioState")));

        // Send a DELETE request to the constructed URL to simulate deleting items
        ValidatableResponse response =
                given() // Start building the request specification
                .when() // Perform the action (in this case, an HTTP DELETE request)
                        .delete(requestUrl) // Specify the URL to send the DELETE request
                .then() // Start defining assertions on the response
                        .log().body(); // Log the response body for debugging purposes

        // Assert that the response status code is 204 (No Content)
        Assert.assertEquals(response.extract().statusCode(), 204);
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
