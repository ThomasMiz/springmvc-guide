package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.concurrent.TimeUnit;

@EnableWebSecurity
@ComponentScan({"ar.edu.itba.paw.webapp.auth"})
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    // Tengo que configurar el UserDetailsService del auth para que sepa como usar nuestra base de datos. Le proveemos
    // al auth un UserDetailsService. ES IMPORTANTÍSIMO ESPECIFICARLO PORQUE SI NO AGARRA E INTENTA ACCEDER A LA DB DE
    // FORMA DIRECTA ASUMIENDO UNA TABLA CON CIERTO ESQUEMA.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        // Spring security pone un filtro a todos los requests y los pasa por las reglas que estamos por especificar.
        // Armamos un "filter chain", de reglas que se verifican de forma ordenada, para ver si un request pasa o no.
        // DEBEMOS CONFIGURAR UN FILTRO EN webapp/WEB-INF/web.xml PARA QUE ESTO FUNCIONE.

        http.sessionManagement()
                // Si la sesión del usuario es inválida (por ej, porque expiró), redirigilo al /login:
                .invalidSessionUrl("/login")

                // Ahora voy a especificar como se autorizan los requests. Notar que se validan en orden especificado:
                .and().authorizeRequests()
                // Especifico que "/login" y "/register" solo son válidas para usuarios anónimos:
                .antMatchers("/login", "/register").anonymous()
                // (Estos dos siguientes los dejo comentados como ejemplo)
                // El endpoint "/posts/review" solo lo pueden acceder usuarios con el rol de EDITOR:
                // .antMatchers("/posts/review").hasRole("EDITOR")
                // El endpoint "user/ban" solo lo pueden acceder usuarios con el rol de USER_ADMIN:
                // .antMatchers("/user/ban").hasRole("USER_ADMIN");
                // Todos los demas accesos a cualquier cosa en "/**" piden solo autentiación (ESTO SIEMPRE AL FINAL!)
                .antMatchers("/**").authenticated()

                // Ahora voy a especificar la lógica del login para que maneje el formulario por nosotros:
                .and().formLogin()
                // Le digo que el endpoint donde se hace login es "/login":
                .loginPage("/login")
                // Le especifico como se llaman los parametros de username y password en el form:
                .usernameParameter("email").passwordParameter("password")
                // En el caso de login exitoso, vamos a redirigir al url "/":
                .defaultSuccessUrl("/", false)
                // El false es del parámetro "alwaysUse", que dice si tiene que forzar ese redirect o si puede ir a
                // otro lado. Por ej, intentaste acceder a /post/review pero te mando a login y despues volvemos

                // Podemos configurar para que se acuerde las sesiones:
                .and().rememberMe()
                // Especificamos que lo haga en base a un parametro "rememberme" en el formulario de login:
                .rememberMeParameter("rememberme")
                // Especificamos el UserDetailsService a utilizar. Usamos un PawUserDetailsService inyectado arriba:
                .userDetailsService(userDetailsService)
                // Podemos especificar el nombre de la cookie de remember me:
                // .rememberMeCookieName("remember-me-cookie")
                // Spring-security usa cookies para trackear sesiones de usuario. Esas cookies son valores
                // criptográficos que se generan con una función de hashing que toma un SALT. Este salt, por default,
                // se genera con un random criptográfico cuando inicia el servidor, pero eso significa que si yo
                // reinicio el servidor, este salt cambia y todas las sesiones que di antes no las puedo validar más!
                // La solución es especificar este salt manualmente a una constante, pero esto debe ser un valor largo
                // y criptográficamente seguro, no un texto cualquiera! Una opción sería pedirle a OpenSSL que genere
                // este string, guardarlo en un archivo de recurso, y cargarlo en runtime.
                .key("NO HAGAS ESTO") // <-- PROBLEMA DE SEGURIDAD, USAR
                // Especificamos la cantidad de tiempo en segundos que dura el remember me:
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))

                // Ahora vamos a configurar el logout:
                .and().logout()
                // La URL para hacer logout es "/logout":
                .logoutUrl("/logout")
                // Y cuando el logout se hace correctamente redirigimos a "/login":
                .logoutSuccessUrl("/login")

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
