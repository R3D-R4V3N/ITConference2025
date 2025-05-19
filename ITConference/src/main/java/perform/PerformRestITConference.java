// Begin modificatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/perform/PerformRestITConference.java
package perform;

import org.springframework.boot.CommandLineRunner; // Importeer CommandLineRunner
import org.springframework.stereotype.Component; // Importeer Component
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import domain.Event;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component // Maak van deze klasse een Spring Component
public class PerformRestITConference implements CommandLineRunner { // Implementeer CommandLineRunner

    // Basis URL van de server die deze client zal aanroepen
    private final String SERVER_URI = "http://localhost:8080/api";
    // WebClient instantie om HTTP requests te verzenden en responses te ontvangen
    private final WebClient webClient = WebClient.create();

    // Verwijder de constructor, we gebruiken nu de run-methode van CommandLineRunner

    @Override
    public void run(String... args) throws Exception { // run-methode wordt uitgevoerd bij opstarten
        System.out.println("---- ---- TESTEN VAN REST API MET REACTIVE WEB CLIENT ---- ----");

        // Test 1: Ophalen van eventgegevens van een gegeven datum
        System.out.println("\n---- ---- GET EVENTS BY DATE (bestaande datum) ---- ----");
        // Gebruik een datum waarvan je weet dat er events zijn, bijv. 7 dagen na vandaag
        LocalDate testDate = LocalDate.now().plusDays(7);
        getEventsByDate(testDate);

        System.out.println("\n---- ---- GET EVENTS BY DATE (niet-bestaande datum) ---- ----");
        LocalDate nonExistentDate = LocalDate.now().plusYears(1); // Datum in de verre toekomst
        getEventsByDate(nonExistentDate);

        // Test 2: Ophalen van een capaciteit van een gegeven lokaal
        System.out.println("\n---- ---- GET LOKAAL CAPACITEIT (bestaand lokaal) ---- ----");
        // Gebruik een lokaal waarvan je weet dat het bestaat, bijv. "A101"
        getLokaalCapaciteit("A101");

        System.out.println("\n---- ---- GET LOKAAL CAPACITEIT (niet-bestaand lokaal) ---- ----");
        getLokaalCapaciteit("ONBESTAAND_LOKAAL");

        System.out.println("\n---- ---- TESTEN VAN REST API VOLTOOID ---- ----");
    }

    // Methode om events op te halen per datum
    private void getEventsByDate(LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        System.out.println("Aanroepen: " + SERVER_URI + "/eventsByDate?date=" + formattedDate);
        webClient.get()
                .uri(SERVER_URI + "/eventsByDate?date=" + formattedDate)
                .retrieve()
                .bodyToFlux(Event.class) // Verwacht een stroom van Event objecten
                .collectList() // Verzamel ze in een lijst
                .doOnSuccess(events -> {
                    if (events.isEmpty()) {
                        System.out.println("Geen evenementen gevonden op " + formattedDate);
                    } else {
                        System.out.println("Evenementen gevonden op " + formattedDate + ":");
                        events.forEach(event -> System.out.println(" - " + event.getNaam() + " in " + event.getLokaal().getNaam()));
                    }
                })
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    System.err.println("Fout: Evenementen niet gevonden op " + formattedDate + " (HTTP 404)");
                    return Mono.empty(); // Ga verder zonder een fout te gooien
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    System.err.println("Fout bij het ophalen van evenementen op " + formattedDate + ": " + ex.getStatusCode() + " " + ex.getResponseBodyAsString());
                    return Mono.empty();
                })
                .onErrorResume(Exception.class, ex -> {
                    System.err.println("Algemene fout bij het ophalen van evenementen: " + ex.getMessage());
                    return Mono.empty();
                })
                .block(); // Blokkeer om te wachten op de resultaten (voor command-line uitvoering)
    }

    // Methode om lokaal capaciteit op te halen
    private void getLokaalCapaciteit(String lokaalNaam) {
        System.out.println("Aanroepen: " + SERVER_URI + "/lokalen/" + lokaalNaam + "/capaciteit");
        webClient.get()
                .uri(SERVER_URI + "/lokalen/" + lokaalNaam + "/capaciteit")
                .retrieve()
                .bodyToMono(Integer.class) // Verwacht een enkel Integer object
                .doOnSuccess(capaciteit -> System.out.println("Capaciteit van lokaal " + lokaalNaam + ": " + capaciteit))
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    System.err.println("Fout: Lokaal " + lokaalNaam + " niet gevonden (HTTP 404).");
                    return Mono.empty(); // Ga verder zonder een fout te gooien
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    System.err.println("Fout bij het ophalen van capaciteit voor lokaal " + lokaalNaam + ": " + ex.getStatusCode() + " " + ex.getResponseBodyAsString());
                    return Mono.empty();
                })
                .onErrorResume(Exception.class, ex -> {
                    System.err.println("Algemene fout bij het ophalen van capaciteit: " + ex.getMessage());
                    return Mono.empty();
                })
                .block(); // Blokkeer om te wachten op de resultaten
    }
}
// Einde modificatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/perform/PerformRestITConference.java