package repository;

import domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Room findByName(String name);

    List<Room> findAllByOrderByNameAsc();

    boolean existsByName(String name);
}
