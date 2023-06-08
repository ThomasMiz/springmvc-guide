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
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

// Con el @ComponentScan(), yo le puedo decir a dónde tiene que ir a buscar componentes, como controllers y services.

@EnableScheduling
@EnableAsync
@EnableTransactionManagement
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
        vr.setPrefix("/WEB-INF/jsp/");
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
        // NOTE: It is recommended you keep these in a separate file such as resources/application.properties

        return ds;
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        // Este transaction manager no nos sirve más! Ahora que tenemos object-relational-mapping de Hibernate
        // necesitamos un transaction manager que entienda JPA.
        // return new DataSourceTransactionManager(ds);

        return new JpaTransactionManager(emf);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        factoryBean.setPackagesToScan("ar.edu.itba.paw.models");
        factoryBean.setDataSource(dataSource());

        final HibernateJpaVendorAdapter jpaAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(jpaAdapter);

        final Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL92Dialect");
        // Con la setting hibernate.hbm2ddl.auto le decimos a hibernate cómo asegurarse que las tablas de la base de
        // datos sean iguales a los modelos que tiene. Si pongo "create", le digo que re-cree las tablas Y SE PIERDEN
        // TODOS LOS DATOS. Si pongo "update", que hace lo mejor que pueda para ALTER-ar las tablas a los modelos.
        // Si agregaste un CHECK (cosa > 0) probablemente lo ignore, pero si agregaste una columna nueva la agrega.
        // Si no queres que haga nada, lo pones en "none".
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        // NOTA: Si decidis usar esto, deberias sacar los @Bean de dataSourceInitializer y databasePopulator, ya que
        // Hibernate se estará encargando de crear las tablas.

        // Esto imprime a STDOUT las consultas SQL (SI LO PONES EN PRODUCCIÓN REPROBAS):
        // properties.setProperty("hibernate.show_sql", "true");
        // properties.setProperty("format_sql", "true");

        factoryBean.setJpaProperties(properties);

        return factoryBean;
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
