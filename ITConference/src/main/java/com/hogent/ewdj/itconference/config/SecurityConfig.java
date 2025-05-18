package com.hogent.ewdj.itconference.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ... UserDetailsService en configureGlobal beans (komen later)

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
                .authorizeHttpRequests(requests -> requests
                        // ... bestaande regels ...

                        .requestMatchers("/events").permitAll() // Publiek toegankelijk
                        .requestMatchers("/login").permitAll() // Login pagina publiek
                        .requestMatchers("/css/**").permitAll() // Statische bestanden
                        .requestMatchers("/js/**").permitAll() // Statische bestanden
                        .requestMatchers("/icons/**").permitAll() // Statische bestanden
                        .requestMatchers("/error").permitAll() // Error pagina

                        // *** Commentarieer deze regel tijdelijk uit voor testdoeleinden ***
                        // .requestMatchers("/events/add").hasRole("ADMIN")

                        // Hier komen later regels voor andere beveiligde pagina's
                        // .requestMatchers("/admin/**").hasRole("ADMIN")
                        // .requestMatchers("/favorites").hasRole("USER")

                        // Als je deze regel uitcommentarieert, vallen /events/add en andere niet-gedefinieerde
                        // URL's onder de .anyRequest().authenticated() regel en vereisen ze nog steeds login.
                        // Om /events/add ook publiek te maken voor tijdelijk testen, voeg je hier .permitAll() toe:
                        .requestMatchers("/events/add").permitAll() // T IJDELIJK: Sta iedereen toe voor testen


                        .anyRequest().authenticated() // Alle andere requests vereisen authenticatie
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/error?errorCode=403")
                );

        return http.build();
    }

    // ... UserDetailsService en PasswordEncoder beans (komen later)
}