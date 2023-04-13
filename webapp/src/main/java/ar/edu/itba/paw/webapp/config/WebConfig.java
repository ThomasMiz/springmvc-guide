package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.concurrent.TimeUnit;

// Con el @ComponentScan(), yo le puedo decir a dónde tiene que ir a buscar componentes, como controllers y services.

@EnableWebMvc
@ComponentScan({"ar.edu.itba.paw.webapp.controller", "ar.edu.itba.paw.services", "ar.edu.itba.paw.persistence"})
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Value("classpath:schema.sql")
    private Resource schemaSql;

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


    // Aca definimos un bean para la persistencia de datos. Este DataSource será inyectado en runtime a la capa de
    // persistencia.
    @Bean
    public DataSource dataSource() {
        final SimpleDriverDataSource ds = new SimpleDriverDataSource();
        ds.setDriverClass(org.postgresql.Driver.class);
        ds.setUrl("jdbc:postgresql://localhost/pawtest"); // We set the address and database to connect to
        ds.setUsername("postgres"); // We set the username and password for the database
        ds.setPassword("postgres");

        return ds;
    }

    // Con esto ubicamos los archivos en WEB-INF/css/* para mapearlos a /css/*, haciéndolos accesibles a clientes
    // Otra alternativa es usando un <servlet-mapping>, explicado en un comentario en el archivo WEB-INF/web.xml.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource ds) {
        final DataSourceInitializer dsi = new DataSourceInitializer();
        dsi.setDataSource(ds);
        dsi.setDatabasePopulator(databasePopulator());

        return dsi;
    }

    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemaSql);

        return populator;
    }

    @Bean
    public MessageSource messageSource() {
        // Para internacionalización, usamos archivos .properties que guardan los strings a usar en diferentes idiomas.
        // Esto es localización, por ende se divide no en idiomas pero en LOCALES, que son idioma y región.

        // Para saber que locale usar, Spring se fija para cada request el header HTTP "Accept-Language", que puede ser
        // por ejemplo, "Accept-Language: en_US, en, es", que significa "quiero que me des el sitio en inglés de EEUU,
        // si no tenes eso inglés (sin importar región), y si no tenés eso dame español. Spring entonces va a, por cada
        // string a traducir, buscar a ver si tiene el string en cada locale por ese orden.

        // También se tiene que definir un "archivo base", el "messages.properties" sin traducción que se usa si no se
        // pide nada en particular. Como para nosotros este está en inglés, no tiene sentido poner otro separado en
        // inglés "messages_en.properties". PERO podemos crear otros para en_US y en_UK para cambiar, por ejemplo,
        // "color" a "colour".

        final ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();

        ms.setCacheSeconds((int) TimeUnit.MINUTES.toSeconds(5));
        ms.setBasename("classpath:i18n/messages");
        ms.setDefaultEncoding(StandardCharsets.UTF_8.name());

        return ms;
    }
}
