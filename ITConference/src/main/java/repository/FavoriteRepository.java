// Begin creatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/repository/FavoriteRepository.java
package repository;

import domain.Event;
import domain.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<MyUser, Long> {

    // Methode om de favoriete events van een gebruiker op te halen, gesorteerd op datum en dan op naam
    @Query("SELECT e FROM MyUser u JOIN u.favoriteEvents e WHERE u.username = :username ORDER BY e.datumTijd ASC, e.naam ASC")
    List<Event> findFavoriteEventsByUsernameOrderByDatumTijdAscNaamAsc(@Param("username") String username);

    // Methode om te controleren of een event al favoriet is bij een gebruiker
    @Query("SELECT COUNT(e) FROM MyUser u JOIN u.favoriteEvents e WHERE u.username = :username AND e.id = :eventId")
    long countFavoriteEventByUsernameAndEventId(@Param("username") String username, @Param("eventId") Long eventId);
}
// Einde creatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/repository/FavoriteRepository.java