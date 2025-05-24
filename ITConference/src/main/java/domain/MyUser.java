package domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity // JPA-entiteit
@Data // Lombok: getters en setters
@Builder // Lombok: builderpatroon
@AllArgsConstructor // Lombok: constructor met alle velden
@NoArgsConstructor // Lombok: standaardconstructor
@Table(name = "users") // tabel 'users'
public class MyUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id // primaire sleutel
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autonoom gegenereerde id
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;

    @ManyToMany // relatie met favoriete events
    @JoinTable(
            name = "user_favorite_events",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "eventId")
    )
    private Set<Event> favoriteEvents = new HashSet<>();

}