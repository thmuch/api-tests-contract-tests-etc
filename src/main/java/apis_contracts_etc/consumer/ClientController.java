package apis_contracts_etc.consumer;

import apis_contracts_etc.consumer.model.ConsumerWetter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClient;

@Controller
public class ClientController {

    private final RestClient restClient;

    public ClientController(
            RestClient.Builder restClientBuilder,
            @Value("${provider.base-url}") String baseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Ruft die Wetter-API auf dem Server auf.
     *
     * @param stadt
     * @return Wetter-Info als Client-Datentyp
     */
    public ConsumerWetter serverAufruf(String stadt) {

        ConsumerWetter ergebnisVomServer =
                restClient.get()
                        .uri("/api/wetter/{stadt}", stadt)
                        .retrieve()
                        .body(ConsumerWetter.class);

        return ergebnisVomServer;
    }
}
