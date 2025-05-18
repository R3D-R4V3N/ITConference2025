package domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity // Geeft aan dat dit een JPA entiteit is
@Data // Lombok annotatie voor getters, setters, equals, hashCode en toString
@NoArgsConstructor // Lombok annotatie voor een no-args constructor (vereist door JPA)
@AllArgsConstructor // Lombok annotatie voor een constructor met alle argumenten
@Table(name = "lokalen") // Specificeert de naam van de databasetabel
public class Lokaal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id // Geeft aan dat dit het primaire sleutelveld is
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Laat de database de waarde genereren (auto-increment)
    private Long id; // Interne ID voor JPA

    @Column(nullable = false, unique = true) // Naam is verplicht en moet uniek zijn in de database kolom
    @NotBlank(message = "{lokaal.naam.notBlank}") // Validatie: mag niet leeg zijn (komt uit resource bundle)
    @Pattern(regexp = "^[A-Za-z]\\d{3}$", message = "{lokaal.naam.pattern}") // Validatie: begint met letter, gevolgd door 3 cijfers (komt uit resource bundle)
    private String naam;

    @Column(nullable = false) // Capaciteit is verplicht
    @NotNull(message = "{lokaal.capaciteit.notNull}") // Validatie: mag niet null zijn (komt uit resource bundle)
    @Min(value = 1, message = "{lokaal.capaciteit.min}") // Validatie: minimum capaciteit (komt uit resource bundle)
    @Max(value = 50, message = "{lokaal.capaciteit.max}") // Validatie: maximum capaciteit (komt uit resource bundle)
    private int capaciteit;

    // Extra constructor indien nodig, afhankelijk van hoe je objecten aanmaakt
    public Lokaal(String naam, int capaciteit) {
        this.naam = naam;
        this.capaciteit = capaciteit;
    }
}