package repository;

import domain.Lokaal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Geeft aan dat dit een Spring Repository component is
public interface LokaalRepository extends JpaRepository<Lokaal, Long> {

    // Spring Data JPA genereert automatisch de implementatie voor standaard methoden

    // Je kunt hier custom find methoden definiëren. Spring Data JPA leidt de query af van de methode naam.
    // Voorbeeld: een methode om een Lokaal te zoeken op naam (wat uniek moet zijn volgens de validatie)
    Lokaal findByNaam(String naam);

    // Voorbeeld: een methode om alle lokalen te vinden en te sorteren op naam
    List<Lokaal> findAllByOrderByNaamAsc();

    // Spring Data JPA kan ook methoden genereren voor validatie checks op unieke velden.
    boolean existsByNaam(String naam);
}