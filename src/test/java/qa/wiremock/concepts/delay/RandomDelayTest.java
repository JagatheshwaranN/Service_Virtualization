package qa.wiremock.concepts.delay;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.DelayDistribution;
import com.github.tomakehurst.wiremock.http.UniformDistribution;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class RandomDelayTest {

    private static final String HOST = "localhost";

    private static final int PORT = 8080;

    public static WireMockServer wireMockServer;

    @BeforeTest
    public void startupServer() {
        wireMockServer = new WireMockServer(PORT);
        WireMock.configureFor(HOST, PORT);
        wireMockServer.start();

            /*
            Other 2 Delay Distribution Types
            ================================
            Create a new instance of the LogNormal class,which generates a lognormal delay with
            a mean of 100 milliseconds and a standard deviation of 0.1.
            DelayDistribution normalDistribution = new LogNormal(100, 0.1);

            Create a new instance of the FixedDelayDistribution class,which generates a fixed
            delay of 100 milliseconds.
            DelayDistribution fixedDistribution = new FixedDelayDistribution(100);
        */

        // Create a new instance of the UniformDistribution class,which generates a uniformly
        // distributed delay between 1000 milliseconds and 3000 milliseconds.
        DelayDistribution uniformDistribution = new UniformDistribution(1000, 3000);

        ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();
        responseDefinitionBuilder.withBodyFile("json/randelay.json");
        WireMock.stubFor(WireMock.get("/random/delay").willReturn(responseDefinitionBuilder.withRandomDelay(uniformDistribution)));
    }

    @AfterTest
    public void shutdownServer() {
        if (wireMockServer.isRunning() && null != wireMockServer) {
            wireMockServer.shutdownServer();
        }
    }

    @Test
    public void testRandomDelay() {
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
