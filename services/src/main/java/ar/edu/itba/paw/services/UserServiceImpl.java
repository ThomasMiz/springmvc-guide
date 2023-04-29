package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// Podemos usar @Transaction para transformar un método en transacción, o podemos ponerle @Transaction a la clase para
// hacer que TODOS Los métodos sean transaccionales.
// NOTAR: Los únicos métodos a los que se les puede aplicar @Transactional son MÉTODOS DE INTERFAZ, ya que la forma en
// la que Spring hace esto es con una CLASE PROXY, que implementa UserService y internamente redirige las llamadas a
// dichos métodos a una instancia de UserServiceImpl. Esto le permite, al llamar cualquier método de UserService,
// ejecutar código antes y/o después. PERO entonces si nosotros, desde adentro de UserServiceImpl, llamamos a uno de
// nuestros métodos, esto no va a funcionar, pues no lo estamos llamando a traves de la clase proxy.
// Ejemplo:
// public void foo() { bar(); }
// @Transactional public void bar() { ... }
// Ambos métodos hacen lo mismo, pero si yo desde afuera de UserServiceImpl llamo a UserService.foo() no va a ser
// transaccional, pero si llamo a bar() si va a serlo.
// Una posible solución es poner @Transactional a toda la clase y listo, pero eso cuesta performance...

// @Transactional // Esto hace que TODOS los métodos de la clase sean transacciones. Acá prefiero hacerlo por-método.
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional // Podes configurarle parámetros (readOnly, timeout, isolation, cómo hace rollback, y más)
    @Override
    public User create(final String email, final String password) {
        return userDao.create(email, passwordEncoder.encode(password));
    }

    @Override
    public Optional<User> findById(long userId) {
        return userDao.findById(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Transactional
    @Override
    public void changePassword(String email, String password) {
        userDao.changePassword(email, passwordEncoder.encode(password));
    }
}
