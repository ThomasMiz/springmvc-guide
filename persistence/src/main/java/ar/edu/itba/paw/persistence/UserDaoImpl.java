package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired // Motor de inyección de dependencias; nos da el DataSource definido en el @Bean de WebConfig.
    public UserDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public User createUser(final String email, final String password) {
        return new User(email, password);
    }

    // READ THE COMMENTS IN findById() IF YOU WANT TO UNDERSTAND WHAT THE FUCK THIS IS
    /*private static final RowMapper<User> ROW_MAPPER = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getString("email"), rs.getString("password"));
        }
    };*/

    private static final RowMapper<User> ROW_MAPPER =
            (rs, rowNum) -> new User(rs.getString("email"), rs.getString("password"));

    @Override
    public Optional<User> findById(long id) {
        // jdbcTemplate.query: nos permite correr un query. El primer parámetro es el SQL, el segundo es para sacar los
        // resultados, hay varias opciones:
        // ResultSetExtractor: te permite procesar el resultset completo
        // RowMapper: te permite convertir cada row a una instancia de una clase particular
        // Vamos a usar un RowMapper para convertir cada row en un User.

        //final List<User> userList = jdbcTemplate.query("SELECT * FROM users WHERE id=" + id, new RowMapper<User>() {
        // This line has an issue! We're vulnerable to SQL injections if we simply concat values into our queries.
        // Maybe not here because we're using a long, but if it were a string it'd be a huge security vulnerability.

        // To fix it, we can use another overload of .query which takes another parameter after the sql, which is a
        // list of objects to replace placeholders in the query. There are two equivalent ways to do this:
        // - Use the .query(String sql, Object[] params, RowMapper) overload
        // - Use the .query(String sql, RowMapper, Object...args) overload
        // We'll use the latter:
        /*final List<User> userList = jdbcTemplate.query("SELECT * FROM users WHERE id=?", new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new User(rs.getString("email"), rs.getString("password"));
            }
        }, id);*/

        // FINAL IMPORTANT NOTE: You shouldn't create a new RowMapper instance each time! That's inefficient!
        // It's better if instead you create that instance ONCE, save it as a static final field, and use that
        // every time instead.

        // Also note: We can turn the new RowMapper<User>() {...} into just a lambda!

        final List<User> userList = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);

        return userList.isEmpty() ? Optional.empty() : Optional.of(userList.get(0));

        // ÚLTIMA ÚLTIMA NOTA: un postrecito de una línea:
        // return jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id).stream().findFirst();
    }
}
