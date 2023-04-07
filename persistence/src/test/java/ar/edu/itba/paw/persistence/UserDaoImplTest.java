package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserDaoImplTest {

    private static final long ID = 1;
    private static final String EMAIL = "pedro@mcpedro.com";
    private static final String PASSWORD = "pedrito3333";

    @Autowired
    private DataSource ds;

    @Autowired
    private UserDaoImpl userDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);

        // Todos los tests arrancan con la tabla de users vacía
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
    }

    @Test
    public void testFindById() throws SQLException {
        // 1. Precondiciones
        jdbcTemplate.execute("INSERT INTO users (id, email, password) VALUES (" + ID +", '" + EMAIL + "', '" + PASSWORD + "')");

        // 2. Ejercitar
        Optional<User> maybeUser = userDao.findById(ID);

        // 3. Postcondiciones
        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(ID, maybeUser.get().getId());
        Assert.assertEquals(EMAIL, maybeUser.get().getEmail());
        Assert.assertEquals(PASSWORD, maybeUser.get().getPassword());
    }

    @Test
    public void testFindByIdDoesNotExist() throws SQLException {
        // 1. Precondiciones

        // 2. Ejercitar
        Optional<User> maybeUser = userDao.findById(ID);

        // 3. Postcondiciones
        Assert.assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testCreate() {
        // 1. Precondiciones

        // 2. Ejercitar
        User user = userDao.create(EMAIL, PASSWORD);

        // 3. Postcondiciones
        Assert.assertNotNull(user);
        Assert.assertEquals(EMAIL, user.getEmail());
        Assert.assertEquals(PASSWORD, user.getPassword());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "users"));
    }

}
