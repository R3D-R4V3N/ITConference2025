package com.hogent.ewdj.itconference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import service.FavoriteService;
import service.FavoriteServiceImpl;

@SpringBootApplication
// Pas deze regel aan: voeg "perform" toe aan de basePackages
@ComponentScan(basePackages = {"com.hogent.ewdj.itconference", "domain", "exceptions", "repository", "service", "service", "util", "validator", "com.hogent.ewdj.itconference.advice", "com.hogent.ewdj.itconference.controller", "perform"})
@EnableJpaRepositories({"repository"})
@EntityScan("domain")
public class ItConferenceApplication{

    public static void main(String[] args) {
        SpringApplication.run(ItConferenceApplication.class, args);
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
