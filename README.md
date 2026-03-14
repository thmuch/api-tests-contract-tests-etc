# API-Tests, Contract-Tests, Consumer-driven Contract-Tests etc.

Der Code in diesem Repository ist die Grundlage für die Live-Coding-Session "API-Tests, Contract-Tests,
Consumer-driven Contract-Tests – alles dasselbe?".

Ziel der Session ist es, die Grundlagen und Konzepte der verschiedenen Test-Varianten zu verstehen.
Es ist kein Tool-Deep-Dive und auch keine Tool-Empfehlung. Die vorgestellten Konzepte lassen sich mit
unterschiedlichen Tools und Frameworks in unterschiedlichen Programmiersprachen umsetzen.

Für die Live-Demo werden Java 25, Spring Boot 4.0 und Pact 4.6 verwendet.


## Vorträge & Folien

- [DevLand 2026](https://www.devland.eu/de/), 13.03.2026 ([PDF](https://thomas-much.de/presentations/ApiTestsContractTests-DevLand-2026.pdf))
- [JavaLand 2026](https://www.javaland.eu/de/home/), 10.03.2026 ([PDF](https://thomas-much.de/presentations/ApiTestsContractTests-JavaLand-2026.pdf))


## Live-Demo

Das Projekt beinhaltet sowohl den [Client/Consumer](src/main/java/apis_contracts_etc/consumer/) als auch den [Server/Provider](src/main/java/apis_contracts_etc/provider/). Beide bringen ihr eigenes Modell mit separaten Datentypen mit. Die Struktur beider Modelle ist zunächst identisch, wird sich aber im Verlauf der Live-Demo verändern.

Folgende Schritte werden in der Live-Demo vorgeführt:

➡️ Der Server/Provider testet seine eigene Wetter-API integrativ, d.h. mit laufendem Server:

- Im [WetterController](src/main/java/apis_contracts_etc/provider/WetterController.java) befindet sich der
  API-Endpoint des Providers.
- Wir starten die [Application](src/main/java/apis_contracts_etc/Application.java), damit der Server/Provider läuft.
- Über die [Swagger-UI](http://localhost:8080/swagger-ui.html) testen wir die API manuell.
- (optional) API über den [IntelliJ-HTTP-Client](server-aufruf.http) testen.

➡️ Der Client/Consumer testet die Provider-API:

- [ClientApiTest](src/test/java/apis_contracts_etc/consumer/ClientApiTest.java) ausführen (sollte grün sein).
- Server (Application) stoppen.
- Test nochmal ausführen (sollte nun rot sein).

➡️ (optional) Der Server/Provider testet seine eigene Wetter-API simuliert, d.h. *ohne* laufenden Server:

- [ServerApiTest](src/test/java/apis_contracts_etc/provider/ServerApiTest.java) ausführen
  (sollte grün sein - ohne laufenden Server).

➡️ Consumer-driven Contract-Testing: Ohne Contracts hat der Provider (noch) nichts zum Verifizieren.

- [ProviderPactTest](src/test/java/apis_contracts_etc/provider/ProviderPactTest.java) ausführen.
- Sollte mit der Fehlermeldung "No Pact files were found to verify" abbrechen, weil es noch keine Contracts im
  entsprechenden [Verzeichnis](src/test/resources/contracts/) gibt.

➡️ Consumer-driven Contract-Testing: Der Consumer testet seinen Aufruf gegen eine simulierte API ("Mock") – und erzeugt nebenbei die Contract-Datei.

- [ConsumerPactTest](src/test/java/apis_contracts_etc/consumer/ConsumerPactTest.java) ausführen (sollte grün sein).
- Contract-Datei sollte nun im entsprechenden [Verzeichnis](src/test/resources/contracts/) vorhanden sein.

➡️ Consumer-driven Contract-Testing: Der Provider kann seine API nun erfolgreich verifizieren und die API kann kompatibel erweitert werden.

- Der [ProviderPactTest](src/test/java/apis_contracts_etc/provider/ProviderPactTest.java) sollte nun grün sein.
- Wir erweitern die Wetter-API, ergänzen im [WetterInfo](src/main/java/apis_contracts_etc/provider/model/WetterInfo.java)-Record
  als drittes Feld die [Wetterlage](src/main/java/apis_contracts_etc/provider/model/Wetterlage.java) und lassen den
  [WetterController](src/main/java/apis_contracts_etc/provider/WetterController.java) konstant `Wetterlage.SONNE` zurückliefern.
- Der [ProviderPactTest](src/test/java/apis_contracts_etc/provider/ProviderPactTest.java) sollte immer noch grün sein!

➡️ Consumer-driven Contract-Testing: Eine inkompatible API-Änderung führt zum Fehlschlag der Verifikation.

- Nun bauen wir eine inkompatible API-Änderung ein.
- In der [Temperatur](src/main/java/apis_contracts_etc/provider/model/Temperatur.java) ändern wir den Datentyp `int`
  auf `String`.
- Im [WetterController](src/main/java/apis_contracts_etc/provider/WetterController.java) ergänzen wir den `temp`-Wert
  um die Zeichenkette " °C".
- Spätestens beim Versuch, den ProviderPactTest auszuführen, wird ein Compiler-Fehler im
  [ServerApiTest](src/test/java/apis_contracts_etc/provider/ProviderPactTest.java) gemeldet. Wir "fixen" den Bug, indem
  wir das `isBetween(-20, 40)` durch `endsWidth(" °C")` ersetzen.
- Der [ProviderPactTest](src/test/java/apis_contracts_etc/provider/ProviderPactTest.java) sollte nun fehlschlagen und
  auf inkompatible Datentypen hinweisen.

➡️ (optional) Consumer-driven Contract-Testing: Wenn der Client einen inkompatiblen Contract erwartet, schlägt die Verifikation beim Provider fehl.

- Die inkompatible API-Änderung von oben zurückdrehen. Nun ändern wir den Consumer-Contract inkompatibel.
- In die [ConsumerTemperatur](src/main/java/apis_contracts_etc/consumer/model/ConsumerTemperatur.java) bauen wir als
  zweites Feld des Records die [ConsumerEinheit](src/main/java/apis_contracts_etc/consumer/model/ConsumerEinheit.java) ein.
- Der [ConsumerPactTest](src/test/java/apis_contracts_etc/consumer/ConsumerPactTest.java) ist zunächst noch grün. Wir
  müssen dort eine weitere Assertion einbauen, die bei der Temperatur die `ConsumerEinheit.CELSIUS` erwartet.
- Nun ist der ConsumerPactTest rot, weil die "gemockte" Antwort oben in der Klasse nicht mehr passt.
- In der LambdaDsl oben in der Klasse ersetzen wir die Zeile für die Temperatur mit folgendem Code:
  ```
  body.object("temperatur", temperatur -> {
      temperatur.integerType("wert", 15);
      temperatur.stringType("einheit", "CELSIUS");
  });
  ```
- Der ConsumerPactTest sollte nun wieder grün sein - und eine neue [Contract-Datei](src/test/resources/contracts/)
  schreiben.
- Der [ProviderPactTest](src/test/java/apis_contracts_etc/provider/ProviderPactTest.java) sollte nun rot sein.


## Weiterführende Links

### Consumer-driven Contract-Testing

- http://martinfowler.com/articles/consumerDrivenContracts.html
- http://innoq.com/de/articles/2016/09/consumer-driven-contracts/
- http://microsoft.github.io/code-with-engineering-playbook/automated-testing/cdc-testing/

### CDC-Testing-Tools

- http://pact.io
- http://spring.io/projects/spring-cloud-contract

### Allgemeines API-Testing

- https://spring.io/guides/gs/testing-web/
- https://spring.io/guides/gs/testing-rest/
