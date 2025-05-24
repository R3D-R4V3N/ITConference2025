package service;

import domain.MyUser;
import domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private MyUserService myUserService;

    @Autowired
    private MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser user = myUserService.findByUsername(username);

        if (user == null) {
            String msg = messageSource.getMessage(
                    "user.notfound",
                    new Object[]{username},
                    LocaleContextHolder.getLocale());
            throw new UsernameNotFoundException(msg);
        }

        return new User(user.getUsername(), user.getPassword(), convertRoleToAuthorities(user.getRole()));
    }

    private Collection<? extends GrantedAuthority> convertRoleToAuthorities(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}