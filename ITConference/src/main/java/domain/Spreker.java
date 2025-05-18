package domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString; // Zorg dat deze import er is

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
// Sluit 'events' uit om StackOverflowError te voorkomen
@ToString(exclude = {"events"}) // Pas deze lijn aan
@Table(name = "sprekers")
public class Spreker implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Interne ID voor JPA

    @Column(nullable = false, unique = true)
    @NotBlank(message = "{spreker.naam.notBlank}")
    private String naam;

    // Relatie met Events - ManyToMany, mappedBy geeft aan dat Event de owning side is
    @ManyToMany(mappedBy = "sprekers")
    private Set<Event> events = new HashSet<>(); // Dit wordt nu uitgesloten in toString()

    // Constructor voor het aanmaken van Spreker objecten zonder ID (voor JPA)
    public Spreker(String naam) {
        this.naam = naam;
    }

    // Getters en setters worden gegenereerd door @Data
    // toString() wordt gegenereerd door @Data met de exclude lijst
}