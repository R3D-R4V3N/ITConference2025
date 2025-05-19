// Begin modificatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/com/hogent/ewdj/itconference/ItConferenceApplication.java
package com.hogent.ewdj.itconference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean; // Importeer @Bean
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Importeer BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder; // Importeer PasswordEncoder
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // Nodig als je WebMvcConfigurer implementeert
import service.FavoriteService; // Importeer FavoriteService
import service.FavoriteServiceImpl; // Importeer FavoriteServiceImpl

@SpringBootApplication
@ComponentScan(basePackages = {"com.hogent.ewdj.itconference", "domain", "exceptions", "repository", "service", "service", "util", "validator", "com.hogent.ewdj.itconference.advice", "com.hogent.ewdj.itconference.config", "com.hogent.ewdj.itconference.controller"})
@EnableJpaRepositories({"repository"}) // Pas dit aan om alle repositories te scannen
@EntityScan("domain")
// Voeg hier eventueel 'implements WebMvcConfigurer' toe als je deze interface gebruikt
public class ItConferenceApplication /* implements WebMvcConfigurer */ {

    public static void main(String[] args) {
        SpringApplication.run(ItConferenceApplication.class, args);
    }

    // Definieer de PasswordEncoder bean hier
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Definieer de FavoriteService bean
    @Bean
    public FavoriteService favoriteService() {
        return new FavoriteServiceImpl();
    }

    // TODO: Implementeer eventuele methoden van WebMvcConfigurer indien nodig
}
// Einde modificatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/com/hogent/ewdj/itconference/ItConferenceApplication.java