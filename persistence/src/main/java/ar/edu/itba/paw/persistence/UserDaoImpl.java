package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Le sacamos esto porque ahora queremos usar el UserDaoJpa.
// @Repository
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert; // En vez de hacer queries de tipo INSERT, usamos este objeto.

    @Autowired // Motor de inyección de dependencias; nos da el DataSource definido en el @Bean de WebConfig.
    public UserDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        // con .usingColumns(); podemos especificar las columnas a usar y otras cosas
    }

    @Override
    public User create(final String email, final String password) {
        // El lo hacemos con jdbcInsert.executeAndReturnKey(), que nos retorna la key de la row insertada.
        // A esta función se le especifica una mapa Map<String, ?>, que es un mapa columna -> valor

        // NUNCA HAGAN ESTO SOTUYO DICE QUE ESTÁ PROHIBIDÍSIMO:
        /*Map<String, Object> data = new HashMap<String, Object>() {{
            put("email", email);
            put("password", password);
        }};*/
        // ESTO CREA UNA SUBCLASE DE HASHMAP Y LLAMA AL PUT EN EL CONSTRUCTOR O SEA ES HORRIBLE ESTÁ MUY MAL

        // Lo que sí está bien hacer es:
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);

        // Sotuyo también dijo que se podía hacer así, pero a mi no me reconoce Map.of():
        // Map<String, Object> data = Map.of("email", email, "password", password);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new User(key.longValue(), email, password);
    }

    // READ THE COMMENTS IN findById() IF YOU WANT TO UNDERSTAND WHAT THE FUCK THIS IS
    /*private static final RowMapper<User> ROW_MAPPER = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getLong("user_id"), rs.getString("email"), rs.getString("password"));
        }
    };*/

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getLong("user_id"),
            rs.getString("email"),
            rs.getString("password")
    );

    @Override
    public Optional<User> findById(long userId) {
        // jdbcTemplate.query: nos permite correr un query. El primer parámetro es el SQL, el segundo es para sacar los
        // resultados, hay varias opciones:
        // ResultSetExtractor: te permite procesar el resultset completo
        // RowMapper: te permite convertir cada row a una instancia de una clase particular
        // Vamos a usar un RowMapper para convertir cada row en un User.

        //final List<User> userList = jdbcTemplate.query("SELECT * FROM users WHERE user_id=" + userId, new RowMapper<User>() {
        // This line has an issue! We're vulnerable to SQL injections if we simply concat values into our queries.
        // Maybe not here because we're using a long, but if it were a string it'd be a huge security vulnerability.

        // To fix it, we can use another overload of .query which takes another parameter after the sql, which is a
        // list of objects to replace placeholders in the query. There are two equivalent ways to do this:
        // - Use the .query(String sql, Object[] params, RowMapper) overload
        // - Use the .query(String sql, RowMapper, Object...args) overload
        // We'll use the latter:
        /*final List<User> userList = jdbcTemplate.query("SELECT * FROM users WHERE user_id=?", new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new User(rs.getString("email"), rs.getString("password"));
            }
        }, userId);*/

        // FINAL IMPORTANT NOTE: You shouldn't create a new RowMapper instance each time! That's inefficient!
        // It's better if instead you create that instance ONCE, save it as a static final field, and use that
        // every time instead.

        // Also note: We can turn the new RowMapper<User>() {...} into just a lambda!

        final List<User> userList = jdbcTemplate.query("SELECT * FROM users WHERE user_id=?", ROW_MAPPER, userId);

        return userList.isEmpty() ? Optional.empty() : Optional.of(userList.get(0));

        // ÚLTIMA ÚLTIMA NOTA: un postrecito de una línea:
        // return jdbcTemplate.query("SELECT * FROM users WHERE user_id=?", ROW_MAPPER, userId).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email).stream().findFirst();
    }

    @Override
    public void changePassword(String email, String password) {
        jdbcTemplate.update("UPDATE users SET password=? WHERE email=?", password, email);
    }
}
