package domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity // Geeft aan dat dit een JPA entiteit is
@Data // Lombok annotatie voor getters, setters, equals, hashCode en toString
@NoArgsConstructor // Lombok annotatie voor een no-args constructor (vereist door JPA)
@AllArgsConstructor // Lombok annotatie voor een constructor met alle argumenten
@Table(name = "sprekers") // Specificeert de naam van de databasetabel
public class Spreker implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id // Geeft aan dat dit het primaire sleutelveld is
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Laat de database de waarde genereren (auto-increment)
    private Long id; // Interne ID voor JPA

    @Column(nullable = false, unique = true) // Spreker naam moet uniek zijn in de database kolom
    @NotBlank(message = "{spreker.naam.notBlank}") // Validatie: mag niet leeg zijn (komt uit resource bundle)
    private String naam;

    // Relatie met Events - ManyToMany, mappedBy geeft aan dat Event de owning side is van de relatie
    // De join tabel wordt beheerd door de Event entiteit
    @ManyToMany(mappedBy = "sprekers")
    private Set<Event> events = new HashSet<>(); // Gebruik Set omdat de volgorde van events voor een spreker meestal niet uitmaakt en duplicaten voorkomen

    // Constructor voor het aanmaken van Spreker objecten zonder ID (voor JPA)
    public Spreker(String naam) {
        this.naam = naam;
    }

    // De @Data annotatie genereert automatisch de getters en setters voor id en naam.
    // Voor de 'events' set genereert @Data ook getters en setters.
}