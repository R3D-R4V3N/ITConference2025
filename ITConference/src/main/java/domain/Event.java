package domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.NumberFormat;
import validator.ValidBeamerCheck;
import validator.ValidConferenceDate;
import validator.ValidEventConstraints;
import validator.ValidSpeakerList;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ValidEventConstraints // valideert unieke naam en tijd
@ValidBeamerCheck // controleert beamercheck
@Entity // JPA-entiteit
@Data // Lombok: genereert getters en setters
@NoArgsConstructor // Lombok: standaardconstructor
@AllArgsConstructor // Lombok: constructor met alle velden
@Table(name = "events") // koppeling naar tabel 'events'
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id // primaire sleutel
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autonoom gegenereerde id
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "{event.naam.notBlank}")
    @Pattern(regexp = "^[A-Za-z].*", message = "{event.naam.pattern}")
    private String naam;

    @Lob
    private String beschrijving;

    @ManyToMany // relatie met sprekers
    @JoinTable(
            name = "event_spreker",
            joinColumns = @JoinColumn(name = "eventId"),
            inverseJoinColumns = @JoinColumn(name = "sprekerId")
    )
    @NotNull(message = "{event.sprekers.notNull}")
    @Size(min = 1, max = 3, message = "{event.sprekers.size}")
    @ValidSpeakerList
    @ToString.Exclude
    @JsonManagedReference
    private List<Spreker> sprekers;

    @ManyToOne(fetch = FetchType.LAZY) // koppeling naar een lokaal
    @JoinColumn(name = "lokaalId", nullable = false)
    @NotNull(message = "{event.lokaal.notNull}")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Lokaal lokaal;

    @Column(nullable = false)
    @NotNull(message = "{event.datumTijd.notNull}")
    @FutureOrPresent(message = "{event.datumTijd.futureOrPresent}")
    @ValidConferenceDate(startDate = "2025-05-18", endDate = "2025-12-31") // datum binnen conferentieperiode
    private LocalDateTime datumTijd;

    @Column(nullable = false)
    @Min(value = 1000, message = "{event.beamercode.size}")
    @Max(value = 9999, message = "{event.beamercode.size}")
    private int beamercode;

    @Transient // niet persistent
    private int beamercheck;

    @Column(nullable = false)
    @NotNull(message = "{event.prijs.notNull}")
    @DecimalMin(value = "9.99", message = "{event.prijs.min}", inclusive = true)
    @DecimalMax(value = "100.00", message = "{event.prijs.max}", inclusive = false)
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal prijs;

    public Event(String naam, String beschrijving, List<Spreker> sprekers, Lokaal lokaal, LocalDateTime datumTijd, int beamercode, BigDecimal prijs) {
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.sprekers = sprekers;
        this.lokaal = lokaal;
        this.datumTijd = datumTijd;
        this.beamercode = beamercode;
        this.prijs = prijs;
    }

    public int calculateCorrectBeamerCheck() {
        return this.beamercode % 97;
    }
}
