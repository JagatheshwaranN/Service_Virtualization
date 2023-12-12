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

    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    public static WireMockServer wireMockServer;

    @BeforeTest
    public void startupServer() {
        wireMockServer = new WireMockServer(PORT);
        try {
            wireMockServer.start();
            WireMock.configureFor(HOST, PORT);
            ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();
            responseDefinitionBuilder.withStatus(200);
            responseDefinitionBuilder.withHeader("Content-Type", "application/json");
            responseDefinitionBuilder.withBodyFile("json/get_user.json");
            WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/user/emp101")).willReturn(responseDefinitionBuilder));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterTest
    public void shutdownServer() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    @Test
    public void mockGetApiTest() {
        String requestUrl = String.format("http://%s:%d/user/emp101", HOST, PORT);

        ValidatableResponse response =
                given()
                .when()
                        .get(requestUrl)
                .then()
                        .assertThat()
                        .statusCode(200)
                        .log()
                        .all();

        Assert.assertEquals(response.extract().statusCode(), 200, "Unexpected status code");
        Assert.assertEquals(response.extract().header("Content-Type"), "application/json", "Unexpected Content-Type");
        Assert.assertEquals(response.extract().jsonPath().get("worker.id"), "EMP101", "Incorrect worker ID");
    }

}
