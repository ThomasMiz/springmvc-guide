package ar.edu.itba.paw.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

// Con el @ComponentScan(), yo le puedo decir a dónde tiene que ir a buscar componentes, como controllers y services.

@EnableWebMvc
@ComponentScan({"ar.edu.itba.paw.webapp.controller", "ar.edu.itba.paw.services", "ar.edu.itba.paw.persistence"})
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    // Con @Bean, yo básicamente le digo a sprint "Che, si alguien te pide un ViewResolver, en vez de darle el default,
    // dale este. Por default, genera una sola instancia y siempre se le da a todos esa misma instancia.
    // Yo no se qué clase va a usar este ViewResolver, y dicha clase tampoco sabe de dónde salió ese ViewResolver.
    @Bean
    public ViewResolver viewResolver() {
        final InternalResourceViewResolver vr = new InternalResourceViewResolver();
        vr.setViewClass(JstlView.class);
        vr.setPrefix("WEB-INF/jsp/");
        vr.setSuffix(".jsp");
        return vr;
    }

    // La otra forma es no definiendo un @Bean, sino agregando el nombre de paquete en el @ComponentScan y dejando que
    // spring detecte solo las clases que tengan @Controller, @Service, @Repository, etc.


    // Con esto ubicamos los archivos en WEB-INF/css/* para mapearlos a /css/*, haciéndolos accesibles a clientes
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
    }
}
