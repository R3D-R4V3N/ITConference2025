package repository;

import domain.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeakerRepository extends JpaRepository<Speaker, Long> {

    Speaker findByName(String name);

    boolean existsByName(String name);

    List<Speaker> findAllByOrderByNameAsc();
}
