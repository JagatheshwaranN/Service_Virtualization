package qa.wiremock.concepts.delay;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class LogNormalRandomDelayTest {

    private static final String HOST = "localhost";

    private static final int PORT = 8080;

    public static WireMockServer wireMockServer;

    @BeforeTest
    public void startupServer() {
        wireMockServer = new WireMockServer(PORT);
        WireMock.configureFor(HOST, PORT);
        wireMockServer.start();
    }

    @AfterTest
    public void shutdownServer() {
        if (wireMockServer.isRunning() && null != wireMockServer) {
            wireMockServer.shutdownServer();
        }
    }

    @Test
    public void testLogNormalRandomDelay() {
        ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();
        responseDefinitionBuilder.withBodyFile("json/randelay.json");
        WireMock.stubFor(WireMock.get("/random/delay").willReturn(responseDefinitionBuilder.withLogNormalRandomDelay(1000, 0.1)));
        ValidatableResponse response =
                given()
                .when()
                        .get("http://localhost:8080/random/delay")
                .then()
                        .statusCode(200);

        System.out.println(response.extract().asPrettyString());
        Assert.assertEquals(response.extract().statusCode(), 200);
    }

}
