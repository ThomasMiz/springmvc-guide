package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) // Le decimos a JUnit que corra los tests con el runner de Mockito
public class UserServiceImplTest {

    private static final long ID = 1;
    private static final String EMAIL = "pedro@mcpedro.com";
    private static final String PASSWORD = "pedrito3333";

    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock // Le pedimos que nos genere una clase mock de UserDao
    private UserDao userDao;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private UserServiceImpl us;

    @Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(userDao.create(anyString(), anyString())).thenReturn(new User(0, EMAIL, PASSWORD));

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        User newUser = us.create(EMAIL, PASSWORD);

        // 3. Postcondiciones
        Assert.assertNotNull(newUser);
        Assert.assertEquals(newUser.getEmail(), EMAIL);
        Assert.assertEquals(newUser.getPassword(), PASSWORD);

        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }

    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
    public void testCreateAlreadyExists() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(userDao.create(eq(EMAIL), eq(PASSWORD))).thenThrow(RuntimeException.class);

        // 2. Ejercitar
        User newUser = us.create(EMAIL, PASSWORD);

        // 3. Postcondiciones
        // (Nada, espero que lo anterior tire exception)
    }

    @Test
    public void testFindById() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(userDao.findById(eq(ID))).thenReturn(Optional.of(new User(ID, EMAIL, PASSWORD)));

        // 2. Ejercitar
        Optional<User> newUser = us.findById(ID);

        // 3. Postcondiciones
        Assert.assertTrue(newUser.isPresent());
        Assert.assertEquals(ID, newUser.get().getUserId());
    }
}
