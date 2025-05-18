package com.hogent.ewdj.itconference.config; // Pas dit package aan naar jouw structuur

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository; // Belangrijk voor CSRF bescherming

@Configuration // Geeft aan dat dit een configuratieklasse is
@EnableWebSecurity // Activeert Spring Security web security support
public class SecurityConfig {

    // Hier zullen we later beans voor UserDetailsService en PasswordEncoder configureren

    @Bean // Definieert een bean voor de security filter chain
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF bescherming is standaard ingeschakeld en belangrijk voor webapplicaties
                // We configureren hier een Repository om het token in de HTTP sessie op te slaan
                .csrf(csrf -> csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
                .authorizeHttpRequests(requests -> requests
                        // Hier definieer je welke URL's toegankelijk zijn en onder welke voorwaarden

                        // Sta toegang toe tot de event overview pagina voor iedereen
                        .requestMatchers("/events").permitAll()
                        .requestMatchers("/login").permitAll() // De login pagina moet altijd publiek zijn
                        .requestMatchers("/css/**").permitAll() // Statische bestanden zoals CSS
                        .requestMatchers("/js/**").permitAll() // Statische bestanden zoals JavaScript
                        .requestMatchers("/icons/**").permitAll() // Statische bestanden zoals iconen
                        .requestMatchers("/error").permitAll() // Error pagina

                        // Hier komen later regels voor andere pagina's die beveiligd moeten worden
                        // Bijvoorbeeld:
                        // .requestMatchers("/admin/**").hasRole("ADMIN") // Alleen Admins bij /admin URL's
                        // .requestMatchers("/favorites").hasRole("USER") // Alleen Users bij /favorites

                        // Voor nu beveiligen we alle andere URL's die hierboven niet expliciet zijn toegestaan
                        .anyRequest().authenticated() // Alle andere requests vereisen authenticatie
                )
                .formLogin(form -> form
                        .loginPage("/login") // Specificeer de custom login pagina URL
                        .defaultSuccessUrl("/dashboard", true) // URL na succesvolle login (later maken we een dashboard)
                        .permitAll() // Sta iedereen toe om de login flow te doorlopen
                )
                // Configureer logout
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL om uit te loggen (Spring Security handelt dit af)
                        .logoutSuccessUrl("/login?logout") // URL na succesvolle logout
                        .permitAll() // Sta iedereen toe om uit te loggen
                )
                // Configureer access denied pagina (voor 403 Forbidden errors)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/error?errorCode=403") // Stuur door naar een error pagina bij 403
                );

        return http.build();
    }

    // Hier komen later methoden voor UserDetailsService en PasswordEncoder
    // @Bean
    // public UserDetailsService userDetailsService() { ... }

    // @Bean
    // public PasswordEncoder passwordEncoder() { ... }
}