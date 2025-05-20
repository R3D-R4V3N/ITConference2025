package com.hogent.ewdj.itconference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver; // Import toevoegen
import org.springframework.web.servlet.config.annotation.InterceptorRegistry; // Import toevoegen
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // Import toevoegen
import org.springframework.web.servlet.i18n.CookieLocaleResolver; // Import toevoegen
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor; // Import toevoegen

import java.util.Locale; // Import toevoegen


@SpringBootApplication
@ComponentScan(basePackages = {
        "com.hogent.ewdj.itconference",
        "domain",
        "exceptions",
        "perform",
        "repository",
        "service",
        "validator"
})
@EnableJpaRepositories({"repository"})
@EntityScan("domain")
public class ItConferenceApplication implements WebMvcConfigurer { // Implementeer WebMvcConfigurer

    public static void main(String[] args) {
        SpringApplication.run(ItConferenceApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // NIEUW: LocaleResolver bean
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver clr = new CookieLocaleResolver();
        clr.setDefaultLocale(new Locale("nl")); // Standaardtaal instellen op Nederlands
        clr.setCookieName("language"); // Optioneel: naam van de cookie instellen
        clr.setCookieMaxAge(3600); // Optioneel: hoe lang de cookie geldig is in seconden
        return clr;
    }

    // NIEUW: LocaleChangeInterceptor bean en registreren
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang"); // De parameter die de taal wijzigt (bijv. ?lang=en)
        registry.addInterceptor(lci);
    }
}