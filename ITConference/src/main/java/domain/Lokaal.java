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

@Entity // JPA-entiteit
@Data // Lombok: genereert getters en setters
@NoArgsConstructor // Lombok: standaardconstructor
@AllArgsConstructor // Lombok: constructor met alle velden
@Table(name = "lokalen") // koppeling naar tabel 'lokalen'
public class Lokaal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id // primaire sleutel
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autonoom gegenereerde id
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "{lokaal.naam.notBlank}")
    @Pattern(regexp = "^[A-Za-z]\\d{3}$", message = "{lokaal.naam.pattern}") // letter gevolgd door drie cijfers
    private String naam;

    @Column(nullable = false)
    @NotNull(message = "{lokaal.capaciteit.notNull}")
    @Min(value = 1, message = "{lokaal.capaciteit.min}")
    @Max(value = 50, message = "{lokaal.capaciteit.max}")
    private int capaciteit;


    public Lokaal(String naam, int capaciteit) {
        this.naam = naam;
        this.capaciteit = capaciteit;
    }
}