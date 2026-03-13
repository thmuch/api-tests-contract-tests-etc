package apis_contracts_etc.consumer;

import apis_contracts_etc.consumer.model.ConsumerWetter;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "wetter-service")
class ConsumerPactTest {

    @Pact(consumer = "wetter-client")
    V4Pact wetterPact(PactBuilder builder) {

        DslPart pactBody = LambdaDsl.newJsonBody(body -> {
            body.object("stadt", stadt -> stadt.stringType("name", "Hamburg"));
            body.object("temperatur", temperatur -> temperatur.integerType("wert", 15));
        }).build();

        return builder
                .usingLegacyDsl()
                .uponReceiving("Wetter für eine Stadt abfragen")
                .path("/api/wetter/Hamburg")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(pactBody)
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "wetterPact")
    void laedtWetterVomProvider(MockServer mockServer) {

        // Given
        ClientController client =
                new ClientController(
                        RestClient.builder(),
                        mockServer.getUrl());

        // When
        ConsumerWetter consumerWetter = client.serverAufruf("Hamburg");

        // Then
        assertThat(consumerWetter).isNotNull();
        assertThat(consumerWetter.stadt().name()).isEqualTo("Hamburg");
        assertThat(consumerWetter.temperatur().wert()).isBetween(-20, 40);
    }
}