package domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity // JPA-entiteit
@Data // Lombok: getters en setters
@NoArgsConstructor // Lombok: standaardconstructor
@AllArgsConstructor // Lombok: constructor met alle velden
@Table(name = "sprekers") // tabel 'sprekers'
public class Spreker implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id // primaire sleutel
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autonoom gegenereerde id
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "{spreker.naam.notBlank}")
    private String naam;

    @ManyToMany(mappedBy = "sprekers") // relatie met events
    @ToString.Exclude
    @JsonBackReference
    private Set<Event> events = new HashSet<>();

    public Spreker(String naam) {
        this.naam = naam;
    }
}