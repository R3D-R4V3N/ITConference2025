package perform;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import domain.Event;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PerformRestITConference implements CommandLineRunner {

    private final String SERVER_URI = "http://localhost:8080/api";
    private final WebClient webClient = WebClient.create();

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        System.out.println("🎯 ---- TESTEN VAN REST API MET REACTIVE WEB CLIENT ----");

        System.out.println("\n📅 ---- GET EVENTS BY DATE (bestaande datum) ----");
        LocalDate testDate = LocalDate.now().plusDays(7);
        getEventsByDate(testDate);

        System.out.println("\n📅 ---- GET EVENTS BY DATE (niet-bestaande datum) ----");
        LocalDate nonExistentDate = LocalDate.now().plusYears(1);
        getEventsByDate(nonExistentDate);

        System.out.println("\n🏢 ---- GET ROOM CAPACITY (existing room) ----");
        getRoomCapacity("A101");

        System.out.println("\n🏢 ---- GET ROOM CAPACITY (non-existent room) ----");
        getRoomCapacity("ONBESTAAND_LOKAAL");

        System.out.println("\n✅ ---- TESTEN VAN REST API VOLTOOID ----");
    }

    private void getEventsByDate(LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        System.out.println("Aanroepen: " + SERVER_URI + "/eventsByDate?date=" + formattedDate);
        webClient.get()
                .uri(SERVER_URI + "/eventsByDate?date=" + formattedDate)
                .retrieve()
                .bodyToFlux(Event.class)
                .collectList()
                .doOnSuccess(events -> {
                    if (events.isEmpty()) {
                        System.out.println("🤷 Geen evenementen gevonden op " + formattedDate);
                    } else {
                        System.out.println("🎉 Evenementen gevonden op " + formattedDate + ":");
                        events.forEach(event -> System.out.println(" - " + event.getNaam() + " in " + event.getLokaal().getNaam()));
                    }
                })
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    System.err.println("🚫 Fout: Evenementen niet gevonden op " + formattedDate + " (HTTP 404)");
                    return Mono.empty();
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    System.err.println("⚠️ Fout bij het ophalen van evenementen op " + formattedDate + ": " + ex.getStatusCode() + " " + ex.getResponseBodyAsString());
                    return Mono.empty();
                })
                .onErrorResume(Exception.class, ex -> {
                    System.err.println("❌ Algemene fout bij het ophalen van evenementen: " + ex.getMessage());
                    return Mono.empty();
                })
                .block();
    }

    private void getRoomCapacity(String roomName) {
        System.out.println("Aanroepen: " + SERVER_URI + "/lokalen/" + roomName + "/capaciteit");
        webClient.get()
                .uri(SERVER_URI + "/lokalen/" + roomName + "/capaciteit")
                .retrieve()
                .bodyToMono(Integer.class)
                .doOnSuccess(capacity -> System.out.println("✅ Capaciteit van lokaal " + roomName + ": " + capacity))
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    System.err.println("🚫 Fout: Lokaal " + roomName + " niet gevonden (HTTP 404).");
                    return Mono.empty();
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    System.err.println("⚠️ Fout bij het ophalen van capaciteit voor lokaal " + roomName + ": " + ex.getStatusCode() + " " + ex.getResponseBodyAsString());
                    return Mono.empty();
                })
                .onErrorResume(Exception.class, ex -> {
                    System.err.println("❌ Algemene fout bij het ophalen van capaciteit: " + ex.getMessage());
                    return Mono.empty();
                })
                .block();
    }
}