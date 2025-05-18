package service;

import domain.MyUser;
import domain.Role; // Zorg dat deze import er is
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User; // Gebruik Spring Security's User klasse
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import repository.MyUserRepository;

import java.util.Collection;
import java.util.Collections;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private MyUserRepository myUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser user = myUserRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Gebruiker niet gevonden met gebruikersnaam: " + username);
        }

        // Converteer je eigen MyUser object naar een Spring Security UserDetails object
        return new User(user.getUsername(), user.getPassword(), convertRoleToAuthorities(user.getRole()));
    }

    // Helper methode om de Role enum om te zetten naar Spring Security GrantedAuthority
    private Collection<? extends GrantedAuthority> convertRoleToAuthorities(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}