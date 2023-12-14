package qa.wiremock.concepts.errors;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.apache.hc.client5.http.fluent.Request;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class RandomDataExceptionTest {

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

    @Test(enabled = false)
    public void testRandomDataException() {
        WireMock.stubFor(WireMock.any(WireMock.urlPathEqualTo("/user/emp103")).willReturn(WireMock.aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));
        Assert.assertThrows(org.apache.hc.core5.http.NoHttpResponseException.class, () -> {
            org.apache.hc.client5.http.fluent.Response response = Request.get("http://localhost:8080/user/emp103").execute();
            System.out.println("Response Body :" + response.returnContent());
        });
    }

}
