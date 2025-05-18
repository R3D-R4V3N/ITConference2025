package domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users") // Kies een geschikte tabelnaam
public class MyUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Gebruik een Long als ID

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Sla de Enum waarde op als String in de database
    @Column(length = 20) // Definieer de maximale lengte voor de rol String
    private Role role; // Gebruik de Role enum die je al hebt

    // TODO: Voeg eventueel andere gebruikersspecifieke velden toe
}