package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class UserDaoJpa implements UserDao {
    @PersistenceContext
    // @PersistenceContext es parecido al autowired, pero tiene información de contexto; da instancias distintas en
    // threads distintos.
    private EntityManager em;

    @Override
    public User create(final String email, final String password) {
        final User user = new User(email, password);
        em.persist(user);
        return user; // OJO: Este user tiene el id en null!
    }

    @Override
    public Optional<User> findById(long userId) {
        return Optional.ofNullable(em.find(User.class, userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // Si quiero usar un query SQL, puedo hacerlo con em.createNativeQuery, pero nosotros vamos a usar createQuery:
        // createQuery no usa SQL sino JPA Query Language (JQL), también conocido como Hibernate Query Language (HQL).
        // Estos lenguajes se parecen a SQL, pero en vez de hablar de tablas y columnas hablan de clases y atributos.
        TypedQuery<User> query = em.createQuery("FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);

        return query.getResultList().stream().findFirst();
    }

    @Override
    public void changePassword(String email, String password) {
        Optional<User> maybeUser = findByEmail(email);
        if (maybeUser.isPresent()) {
            final User user = maybeUser.get();
            user.setPassword(password);
            em.persist(user);
        }
    }
}
