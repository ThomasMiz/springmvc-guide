package ar.edu.itba.paw.persistence.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@ComponentScan("ar.edu.itba.paw.persistence")
@Configuration
public class TestConfig {

    @Value("classpath:hsqldb.sql") // Le pedimos que nos traiga este archivo en resources
    private Resource hsqldbSql; // (leer el comentario enorme en dataSource() para entender esto

    @Value("classpath:schema.sql")
    private Resource schemaSql;

    @Bean
    public DataSource dataSource() {
        final SimpleDriverDataSource ds = new SimpleDriverDataSource();

        ds.setDriverClass(org.hsqldb.jdbcDriver.class);
        ds.setUrl("jdbc:hsqldb:mem:pawtest");
        ds.setUsername("ha");
        ds.setPassword("");

        // Por default HSQLDB no tiene soporte para cosas que estamos usando, como por ejemplo el tipo de dato SERIAL
        // en la columna "id" de la tabla "users". Peero HSQLDB nos permite interpretar estas cosas correctamente
        // pidiéndole que emule alguna base de datos en particular. Por suerte, esto incluye soporte para PostgreSQL.

        // OBVIAMENTE ESTO NO SIMULA FULL POSTGRES, Cosas como triggers y funciones NO ESTÁN.
        // Hay dos formas de usar esto:
        // 1. Agregar a la clase test (ej. UserDaoImplTest) un
        //    @Sql(scripts = { "classpath:hsqldb.sql", "classpath:schema.sql" }) siendo estos archivos los que
        //    agregamos en persistence/src/test/resources/hsqldb.sql y persistence/src/main/resources/schema.sql
        // 2. Como queremos correr schema.sql también en runtime, usamos un DataSourceInitializer. Definimos un @Bean
        //    siguiente a esta función que inicializa una base de datos.

        return ds;
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
        populator.addScript(hsqldbSql);
        populator.addScript(schemaSql);

        return populator;
    }
}
