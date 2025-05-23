package com.hogent.ewdj.itconference.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/events").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/icons/**").permitAll()
                        .requestMatchers("/error").permitAll() // Laat error pagina toe
                        .requestMatchers("/changeLocale").permitAll()
                        .requestMatchers("/events/add").hasRole("ADMIN")
                        .requestMatchers("/events/edit/**").hasRole("ADMIN")
                        .requestMatchers("/lokalen/add").hasRole("ADMIN")
                        .requestMatchers("/lokalen").hasRole("ADMIN")
                        .requestMatchers("/favorites").hasRole("USER")
                        .requestMatchers("/favorites/add").hasRole("USER")
                        .requestMatchers("/favorites/remove").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/events", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        // Gebruik flash attribute voor logout message
                        .logoutSuccessHandler((request, response, authentication) -> {
                            request.getSession().setAttribute("message", "You have been logged out successfully."); // Direct in session plaatsen voor redirect
                            response.sendRedirect("/login");
                        })
                        .permitAll()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        // Gebruik flash attributes voor error messages
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            request.getSession().setAttribute("errorMessage", "Access is denied"); // Direct in session plaatsen voor redirect
                            request.getSession().setAttribute("errorCode", "403"); // Direct in session plaatsen voor redirect
                            response.sendRedirect("/error");
                        })
                );

        return http.build();
    }
}