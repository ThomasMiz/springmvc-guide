package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return Optional.ofNullable(em.getReference(User.class, userId));
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
    public List<User> getAll(int pageNumber, int pageSize) {
        // final TypedQuery<User> query = em.createQuery("FROM User", User.class);
        // query.setMaxResults(pageSize);
        // query.setFirstResult((pageNumber - 1) * pageSize);
        // return query.getResultList();
        // HACER ESTO ASÍ ES PELIGROSO! El tema es que setMaxResults y setFirstResult hablan en términos de ROWS, pero
        // el query habla en términos de entidades y el mapeno no necesariamente es 1 a 1.
        // Si yo estoy trayendo mis users con listas de issues, puede ser que haga un OUTER JOIN para traer ambos en
        // una sola query, en ese caso si usamos setMaxResults y setFirstResults pueden cortar el resultset en el medio
        // de una lista!
        // Pero si yo solo estoy haciendo "SELECT * FROM users", el mapeo filas-entidades seguro es 1 a 1 y esto no
        // va a tener ningún problema.

        // Para evitar este error, Hibernate hizo que cuando la query subyacente precisa hacer un JOIN, entonces en vez
        // de usar LIMIT ? OFFSET ?, va a CARGAR LA QUERY ENTERA A MEMORIA y después aplicar paginación.
        // SI HACES ESTO ESTÁ MAL. ES TERRIBLEMENTE INEFICIENTE.

        // Y si pensas "Ah, pero no pasa nada, mi 'FROM User' no hace joins, no tengo de qué preocuparme!" entonces
        // te expones al riesgo de que si en un futuro si empieza a hacer JOINs, sin warning ni nada caes en el
        // problema de eficiencia. No podes confiar en que no hacen JOINs!

        // Para paginar entonces vamos a usar el modelo de "1+1 queries". Hacemos dos queries; uno de paginación y otro
        // de traer los datos. La query de paginación es nativeQuery, es SQL, entonces me aseguro 100% que no hay JOINs.
        //
        Query nativeQuery = em.createNativeQuery("SELECT user_id FROM users");
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((pageNumber - 1) * pageSize);

        final List<Long> idList = (List<Long>) nativeQuery.getResultList()
                .stream().map(n -> (Long)((Number)n).longValue()).collect(Collectors.toList());

        final TypedQuery<User> query = em.createQuery("FROM User WHERE userId IN :ids", User.class);
        query.setParameter("ids", idList);

        return query.getResultList();
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
