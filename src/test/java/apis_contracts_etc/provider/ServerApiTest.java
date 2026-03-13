package apis_contracts_etc.provider;

import apis_contracts_etc.provider.model.WetterInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@SpringBootTest
//@WebMvcTest(WetterController.class)
@AutoConfigureRestTestClient
class ServerApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    void laedtWetterVonEigenerApi() {

        // When
        WetterInfo wetterInfo = restTestClient.get()
                .uri("/api/wetter/{stadt}", "Hamburg")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(WetterInfo.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertThat(wetterInfo).isNotNull();
        assertThat(wetterInfo.stadt().name()).isEqualTo("Hamburg");
        assertThat(wetterInfo.temperatur().wert()).isBetween(-20, 40);
    }
}
