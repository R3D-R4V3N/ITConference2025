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

@SpringBootApplication
@ComponentScan(basePackages = {"com.hogent.ewdj.itconference", "domain", "exceptions", "repository", "service", "util", "validator", "com.hogent.ewdj.itconference.advice", "com.hogent.ewdj.itconference.config", "com.hogent.ewdj.itconference.controller"})
@EnableJpaRepositories("repository")
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

    // TODO: Implementeer eventuele methoden van WebMvcConfigurer indien nodig
}