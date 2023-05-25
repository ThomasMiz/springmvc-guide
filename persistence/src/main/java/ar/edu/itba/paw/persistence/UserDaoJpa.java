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

    public void changePassword(User user, String password) {
        // Si yo quiero hacer un changePassword que tome un User, no puedo simplemente hacer
        // user.setPassword(password);
        // Porque si no estoy dentro de un @Transactional, esto cambia el modelo en memoria pero no toca la db.
        // Una alternativa es decirle al EntityManager que incorpore esta entidad al contexto de persistencia:
        user = em.merge(user);
        // OJO! merge() retorna una nueva instancia! Ahora si puedo hacer el setPassword:
        user.setPassword(password);
        // Si sus métodos siempre usan @Transactional para este tipo de operaciones, nunca van a necesitar merge.
    }
}
