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

    @Query("SELECT e FROM MyUser u JOIN u.favoriteEvents e WHERE u.username = :username ORDER BY e.datumTijd ASC, e.naam ASC")
    List<Event> findFavoriteEventsByUsernameOrderByDatumTijdAscNaamAsc(@Param("username") String username);

    @Query("SELECT COUNT(e) FROM MyUser u JOIN u.favoriteEvents e WHERE u.username = :username AND e.id = :eventId")
    long countFavoriteEventByUsernameAndEventId(@Param("username") String username, @Param("eventId") Long eventId);

    @Query("SELECT u FROM MyUser u JOIN u.favoriteEvents e WHERE e.id = :eventId")
    List<MyUser> findUsersByFavoriteEventId(@Param("eventId") Long eventId);
}