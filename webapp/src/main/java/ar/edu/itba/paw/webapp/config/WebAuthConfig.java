package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.TimeUnit;

@EnableWebSecurity
@ComponentScan({"ar.edu.itba.paw.webapp.auth"})
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    // Hasta ahora la contraseña la guardamos en texto plano. Esto está recontra mal, y lo vamos a solucionar guardando
    // la contraseña hasheada con bcrypt. Esto lo vamos a hacer con la interfaz PasswordEncoder.
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Hay tres implementaciones default de PasswordEncoder. La primera es NoOpPasswordEncoder, que no hace nada,
        // la segunda es StandardPasswordEncoder que permite utilizar algoritmos que trae spring-security por default.
        // La tercera y la que vamos a usar es BCryptPasswordEncoder, la recomendada por Spring.
        return new BCryptPasswordEncoder();
        // Nota: Las contraseñas hasheadas con bcrypt siguen el patron "\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}".
        // Si queremos usar regex, podemos usar la clase java.util.regex.Pattern.
        // Si nuestra db ya tiene contraseñas en texto plana y queremos migrar a bcrypt, podemos usar esta regex para
        // fijarnos cuando alguien hace login si la contraseña guardada no matchea ese patrón, y si no lo hace la
        // hasheamos y guardamos antes de proceguir.
    }

    // Tengo que configurar el UserDetailsService del auth para que sepa como usar nuestra base de datos. Le proveemos
    // al auth un UserDetailsService. ES IMPORTANTÍSIMO ESPECIFICARLO PORQUE SI NO AGARRA E INTENTA ACCEDER A LA DB DE
    // FORMA DIRECTA ASUMIENDO UNA TABLA CON CIERTO ESQUEMA.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Especificamos que la autenticación se hace con el userDetailsService, y especificamos nuestro
        // PasswordEncoder para que sepa que estamos hasheando las contraseñas con bcrypt.
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        // Spring security pone un filtro a todos los requests y los pasa por las reglas que estamos por especificar.
        // Armamos un "filter chain", de reglas que se verifican de forma ordenada, para ver si un request pasa o no.
        // DEBEMOS CONFIGURAR UN FILTRO EN webapp/WEB-INF/web.xml PARA QUE ESTO FUNCIONE.

        http.sessionManagement()
                // No usamos más el .invalidSessionUrl(). No vamos a usar más endpoints de /login o /register, el login
                // se puede hacer desde cualquier endpoint, y el register es un POST a /users. Es un API REST!
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // Ahora voy a especificar como se autorizan los requests. Notar que se validan en orden especificado:
                .and().authorizeRequests()
                // Especifico que "/login" y "/register" solo son válidas para usuarios anónimos:
                .antMatchers("/login", "/register").anonymous()
                // (Estos dos siguientes los dejo comentados como ejemplo)
                // El endpoint "/posts/review" solo lo pueden acceder usuarios con el rol de EDITOR:
                // .antMatchers("/posts/review").hasRole("EDITOR")
                // El endpoint "user/ban" solo lo pueden acceder usuarios con el rol de USER_ADMIN:
                // .antMatchers("/user/ban").hasRole("USER_ADMIN");
                // Este tipo de filtros de "o si o no" no siempre es suficiente. A veces queremos que, por ejemplo,
                // todos los EDITORs puedan entrar a /posts/review, pero también el usuario que creó el post. Para esto
                // existe un Expression Language en spring security que te permite definir cosas más precisas:
                // .antMatchers("/posts/review").access("@AccessHelper.canEdit") // No los vamos a ver en clase igual

                // Todos los demas accesos a cualquier cosa en "/**" piden solo autentiación (ESTO SIEMPRE AL FINAL!)
                .antMatchers("/**").permitAll() // POR SIMPLICIDAD POR AHORA LO DEJAMOS EN PERMITR ALL



                // Ahora vamos a configurar el manejo de exceptions:
                .and().exceptionHandling()
                // En el caso de una excepción, redirigí a la página de access denied, que es "/errors/403":
                .accessDeniedPage("/errors/403")
                // IMPORTANTE: Configuramos en el WebSecurity abajo que todos puedan acceder a 403 sin problemas.

                // Deshabilitar las reglas de cross-site-request-forgery. No explicaron por qué lo necesitamos:
                .and().csrf().disable();

        // Notemos igual que spring security no sabe cómo guardamos estos datos para persistencia. Para esto, vamos a
        // tener que inyectarle algo de lógica para que sepa cómo usar nuestra base de datos. Para esto, tenemos que
        // implementar la interfaz org.springframework.security.core.userdetails.UserDetailsService.
        // Ver: ar.edu.itba.paw.webapp.auth.PawUserDetailsService.java
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        // Le digo al WebSecurity que ignore estos directorios. De esta forma si viene un request
        // a uno de estos paths, no va a validar accesos. Cualquiera puede acceder a estos recursos.
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "favicon.ico", "/errors/**");
    }
}
