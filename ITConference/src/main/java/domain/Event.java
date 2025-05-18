package domain;

// ... andere imports

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString; // Zorg dat deze import er is
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import validator.ValidBeamerCheck;
import validator.ValidConferenceDate;
import validator.ValidEventConstraints;
import validator.ValidSpeakerList;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ValidEventConstraints
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
// Sluit 'sprekers' en 'lokaal' uit om StackOverflowError te voorkomen.
// Andere collecties zoals 'performances', 'tickets', 'region', 'genre'
// die mogelijk in andere relaties voorkomen, ook uitsluiten indien bidirectioneel.
@ToString(exclude = {"sprekers", "lokaal"}) // Pas deze lijn aan
@Table(name = "events")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "{event.naam.notBlank}")
    @Pattern(regexp = "^[A-Za-z].*", message = "{event.naam.pattern}")
    private String naam;

    @Lob
    private String beschrijving;

    @ManyToMany
    @JoinTable(
            name = "event_spreker",
            joinColumns = @JoinColumn(name = "eventId"),
            inverseJoinColumns = @JoinColumn(name = "sprekerId")
    )
    @Size(min = 1, max = 3, message = "{event.sprekers.size}")
    @ValidSpeakerList
    private List<Spreker> sprekers; // Dit veld wordt nu uitgesloten in toString()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lokaalId", nullable = false)
    @NotNull(message = "{event.lokaal.notNull}")
    private Lokaal lokaal; // Dit wordt nu uitgesloten in toString()

    @Column(nullable = false)
    @NotNull(message = "{event.datumTijd.notNull}")
    @FutureOrPresent(message = "{event.datumTijd.futureOrPresent}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    // ** UPDATED: Set a future conference period that includes your event dates **
    @ValidConferenceDate(startDate = "2025-05-18", endDate = "2025-12-31") // Pas datums aan naar de werkelijke conferentieperiode
    private LocalDateTime datumTijd;

    @Column(nullable = false)
    @Min(value = 1000, message = "{event.beamercode.size}")
    @Max(value = 9999, message = "{event.beamercode.size}")
    private int beamercode;

    @Transient
    private int beamercheck; // Dit veld wordt niet gepersisteerd

    @Column(nullable = false)
    @NotNull(message = "{event.prijs.notNull}")
    @DecimalMin(value = "9.99", message = "{event.prijs.min}", inclusive = true)
    @DecimalMax(value = "100.00", message = "{event.prijs.max}", inclusive = false)
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal prijs;

    // Constructor for the creation of Event objects without ID (for JPA)
    public Event(String naam, String beschrijving, List<Spreker> sprekers, Lokaal lokaal, LocalDateTime datumTijd, int beamercode, BigDecimal prijs) {
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.sprekers = sprekers;
        this.lokaal = lokaal;
        this.datumTijd = datumTijd;
        this.beamercode = beamercode;
        this.prijs = prijs;
    }

    // Getters en setters worden gegenereerd door @Data
    // toString() wordt gegenereerd door @Data met de exclude lijst

    public int getBeamercheck() {
        return beamercheck;
    }

    public void setBeamercheck(int beamercheck) {
        this.beamercheck = beamercheck;
    }

    public int calculateCorrectBeamerCheck() {
        return this.beamercode % 97;
    }
}