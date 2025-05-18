package domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import validator.ValidBeamerCheck; // We gaan deze custom validator later aanmaken
import validator.ValidConferenceDate; // We gaan deze custom validator later aanmaken
import validator.ValidEventConstraints;
import validator.ValidSpeakerList; // We gaan deze custom validator later aanmaken

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ValidEventConstraints
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
// We voegen hier later eventuele class-level validators toe
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "{event.naam.notBlank}")
    @Pattern(regexp = "^[A-Za-z].*", message = "{event.naam.pattern}") // Validatie: naam begint met een letter
    private String naam;

    @Lob // Voor potentieel langere tekst
    private String beschrijving; // Beschrijving is optioneel, dus geen @NotBlank

    // Relatie met sprekers - ManyToMany omdat een spreker meerdere events kan hebben en een event meerdere sprekers
    // We moeten hier nog een Spreker entiteit voor maken
    @ManyToMany
    @JoinTable(
            name = "event_spreker",
            joinColumns = @JoinColumn(name = "eventId"),
            inverseJoinColumns = @JoinColumn(name = "sprekerId")
    )
    @Size(min = 1, max = 3, message = "{event.sprekers.size}") // Maximaal 3 sprekers
    @ValidSpeakerList // Custom validator om te controleren op dubbele sprekers
    private List<Spreker> sprekers; // Gebruik List om de volgorde te behouden indien nodig

    // Relatie met Lokaal - ManyToOne omdat een event plaatsvindt in één lokaal, maar een lokaal meerdere events kan hosten
    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading is vaak efficiënter
    @JoinColumn(name = "lokaalId", nullable = false) // Foreign key kolom
    @NotNull(message = "{event.lokaal.notNull}") // Validatie: lokaal is verplicht
    private Lokaal lokaal;

    @Column(nullable = false)
    @NotNull(message = "{event.datumTijd.notNull}")
    @FutureOrPresent(message = "{event.datumTijd.futureOrPresent}") // Event moet in de toekomst of nu plaatsvinden
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") // Formatteer voor HTML input type="datetime-local"
    @ValidConferenceDate(startDate = "2025-03-01", endDate = "2025-03-31") // Custom validator om te controleren of datum binnen conferentieperiode valt
    private LocalDateTime datumTijd;

    @Column(nullable = false)
    @Min(value = 1000, message = "{event.beamercode.size}") // Viercijferige code
    @Max(value = 9999, message = "{event.beamercode.size}")
    private int beamercode;

    @Transient // Dit veld wordt niet in de database opgeslagen

    private int beamercheck; // Wordt berekend, niet opgeslagen

    @Column(nullable = false)
    @NotNull(message = "{event.prijs.notNull}")
    @DecimalMin(value = "9.99", message = "{event.prijs.min}", inclusive = true) // Prijs >= 9.99
    @DecimalMax(value = "100.00", message = "{event.prijs.max}", inclusive = false) // Prijs < 100
    @NumberFormat(style = NumberFormat.Style.CURRENCY) // Kan gebruikt worden voor weergave, afhankelijk van locale
    private BigDecimal prijs;

    // Constructor voor het aanmaken van Event objecten zonder ID (voor JPA)
    public Event(String naam, String beschrijving, List<Spreker> sprekers, Lokaal lokaal, LocalDateTime datumTijd, int beamercode, BigDecimal prijs) {
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.sprekers = sprekers;
        this.lokaal = lokaal;
        this.datumTijd = datumTijd;
        this.beamercode = beamercode;
        this.prijs = prijs;
        // De beamercheck wordt niet via de constructor ingesteld, maar via de setter of validatie.
    }

    // Getter en Setter voor beamercheck (nodig voor validatie binding)
    public int getBeamercheck() {
        return beamercheck;
    }

    public void setBeamercheck(int beamercheck) {
        this.beamercheck = beamercheck;
    }

    // Methode om de correcte beamercheck te berekenen
    public int calculateCorrectBeamerCheck() {
        return this.beamercode % 97;
    }
}