package repository;

import domain.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser, Long> {

    // Methode om een gebruiker te vinden op basis van gebruikersnaam
    MyUser findByUsername(String username);
}