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

@ValidEventConstraints
@ValidBeamerCheck
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "{event.naam.notBlank}")
    @Pattern(regexp = "^[A-Za-z].*", message = "{event.naam.pattern}")
    private String name;

    @Lob
    private String description;

    @ManyToMany
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
    private List<Speaker> speakers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lokaalId", nullable = false)
    @NotNull(message = "{event.lokaal.notNull}")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Room room;

    @Column(nullable = false)
    @NotNull(message = "{event.datumTijd.notNull}")
    @FutureOrPresent(message = "{event.datumTijd.futureOrPresent}")
    @ValidConferenceDate(startDate = "2025-05-18", endDate = "2025-12-31")
    private LocalDateTime dateTime;

    @Column(nullable = false)
    @Min(value = 1000, message = "{event.beamercode.size}")
    @Max(value = 9999, message = "{event.beamercode.size}")
    private int beamerCode;

    @Transient
    private int beamerCheck;

    @Column(nullable = false)
    @NotNull(message = "{event.prijs.notNull}")
    @DecimalMin(value = "9.99", message = "{event.prijs.min}", inclusive = true)
    @DecimalMax(value = "100.00", message = "{event.prijs.max}", inclusive = false)
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal price;

    public Event(String name, String description, List<Speaker> speakers, Room room, LocalDateTime dateTime, int beamerCode, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.speakers = speakers;
        this.room = room;
        this.dateTime = dateTime;
        this.beamerCode = beamerCode;
        this.price = price;
    }

    public int calculateCorrectBeamerCheck() {
        return this.beamerCode % 97;
    }
}
