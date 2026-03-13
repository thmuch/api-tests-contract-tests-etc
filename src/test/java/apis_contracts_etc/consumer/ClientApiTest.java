package apis_contracts_etc.consumer;

import apis_contracts_etc.consumer.model.ConsumerWetter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class ClientApiTest {

    @Test
    void laedtWetterVomServer() {

        // Given
        ClientController client =
                new ClientController(
                        RestClient.builder(),
                        "http://localhost:8080");

        // When
        ConsumerWetter consumerWetter = client.serverAufruf("Hamburg");

        // Then
        assertThat(consumerWetter).isNotNull();
        assertThat(consumerWetter.stadt().name()).isEqualTo("Hamburg");
        assertThat(consumerWetter.temperatur().wert()).isBetween(-20, 40);
    }
}
