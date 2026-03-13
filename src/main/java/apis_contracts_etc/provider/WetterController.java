package apis_contracts_etc.provider;

import apis_contracts_etc.provider.model.Stadt;
import apis_contracts_etc.provider.model.Temperatur;
import apis_contracts_etc.provider.model.WetterInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @see <a href="http://localhost:8080/swagger-ui.html">Swagger UI</a>
 */
@RestController
public class WetterController {

    @GetMapping("/api/wetter/{stadt}")
    public WetterInfo wetter(@PathVariable String stadt) {

        int temp = ThreadLocalRandom.current().nextInt(-10, 30);

        return new WetterInfo(
                new Stadt(stadt),
                new Temperatur(temp)
        );
    }
}
