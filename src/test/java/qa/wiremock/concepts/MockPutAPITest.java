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

public class MockPutAPITest {

    private static final String HOST = "localhost";

    private static final int PORT = 8080;

    public static WireMockServer wireMockServer;

    @BeforeTest
    public void startupServer() {
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
        WireMock.configureFor(HOST, PORT);
        ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();
        responseDefinitionBuilder.withStatus(200);
        responseDefinitionBuilder.withHeader("Content-Type", "application/json");
        responseDefinitionBuilder.withBodyFile("json/update_user.json");
        WireMock.stubFor(WireMock.put(WireMock.urlMatching("/user/update/.*")).withRequestBody(WireMock.equalToJson("""
                {
                  "name": "John Doe",
                  "location": "New York",
                  "phone": "123-456-7890",
                  "address": {
                    "city": "MiddleTown",
                    "state": "New York",
                    "zipcode": "10001",
                    "country": "United States"
                  }
                }""")).willReturn(responseDefinitionBuilder));
    }

    @AfterTest
    public void shutdownServer() {
        if (wireMockServer.isRunning() && null != wireMockServer) {
            wireMockServer.shutdownServer();
        }
    }

    @Test
    public void mockPutApiTest() {

        String payloadJson = """
                                {
                                  "name": "John Doe",
                                  "location": "New York",
                                  "phone": "123-456-7890",
                                  "address": {
                                    "city": "MiddleTown",
                                    "state": "New York",
                                    "zipcode": "10001",
                                    "country": "United States"
                                  }
                                }""";
        ValidatableResponse response =
                given()
                        .body(payloadJson)
                .when()
                        .put("http://localhost:8080/user/update/emp101")
                .then()
                        .assertThat()
                        .statusCode(200)
                        .log()
                        .all();

        Assert.assertEquals(response.extract().statusCode(), 200);
        Assert.assertEquals(response.extract().header("Content-Type"), "application/json");
        Assert.assertEquals(response.extract().jsonPath().get("worker.id"), "EMP101");
        Assert.assertEquals(response.extract().jsonPath().get("worker.name"), "John Doe");
        Assert.assertEquals(response.extract().jsonPath().get("worker.address.city"), "MiddleTown");
        Assert.assertEquals(response.extract().jsonPath().get("worker.address.country"), "United States");
        Assert.assertEquals(response.extract().jsonPath().get("worker.updatedAt"), "2023-11-04T04:48:52.454Z");
    }

}
