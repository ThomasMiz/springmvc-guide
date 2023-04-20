package ar.edu.itba.paw.webapp.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

// Esta clase nos permite guardar los datos de un models.User en un userdetails.User, una clase de spring security.
// Una alternativa es hacer nuestra propia clase que implemente la interfaz userdetails.UserDetails.
public class PawAuthUserDetails extends User {
    public PawAuthUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public PawAuthUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }
}
