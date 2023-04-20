package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

// Para que el motor de dependencias de spring (por ej, el @Autowired) funcione en esta clase, necesitamos dos cosas:
// 1. Que tenga el @Component o un @Algo donde Algo extiende a Component
// Que esté dentro de un paquete en el @ComponentScan de una clase de @Configuration

@Component
public class PawUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService us;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = us.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("No user for email " + username));

        // Creamos la lista de roles que tiene este usuario
        final Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_EDITOR"));
        authorities.add(new SimpleGrantedAuthority("ROLE_USER_ADMIN"));

        // Creamos una implementación de UserDetails con los datos de nuestro usuario.
        return new PawAuthUserDetails(user.getEmail(), user.getPassword(), authorities);

        // Mirar el otro constructor de PawAuthUser. Tiene una banda de parámetros para especificar si el usuario está
        // habilitado, expirado, o blockeado.
    }
}
