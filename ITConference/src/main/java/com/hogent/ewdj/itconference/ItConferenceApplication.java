// Begin modificatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/com/hogent/ewdj.itconference/ItConferenceApplication.java
package com.hogent.ewdj.itconference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import service.FavoriteService;
import service.FavoriteServiceImpl;
// Verwijder: import perform.PerformRestITConference;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hogent.ewdj.itconference", "domain", "exceptions", "repository", "service", "service", "util", "validator", "com.hogent.ewdj.itconference.advice", "com.hogent.ewdj.itconference.controller"}) // Verwijder "perform"
@EnableJpaRepositories({"repository"})
@EntityScan("domain")
public class ItConferenceApplication /* implements WebMvcConfigurer */ {

    public static void main(String[] args) {
        SpringApplication.run(ItConferenceApplication.class, args);

        // Verwijder dit hele blok:
        /*
        try {
            // Geef de server even de tijd om te starten (dit is een simpele benadering, geen garantie)
            Thread.sleep(5000); // Wacht 5 seconden
            new PerformRestITConference();
        } catch (Exception e) {
            System.err.println("Fout bij het uitvoeren van de REST client: " + e.getMessage());
            e.printStackTrace();
        }
        */
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FavoriteService favoriteService() {
        return new FavoriteServiceImpl();
    }
}
// Einde modificatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/com/hogent/ewdj.itconference/ItConferenceApplication.java