package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.List;

// Hay tres formas de mappear herencia del modelo objetos al modelo relacional. Mezclar todas las entidades
// en una sola tabla (SINGLE_TABLE), con una tabla por entidad concreta (la tabla de Patient tendría los
// mismos campos que la tabla de user, más algunos otros) (TABLE_PER_CLASS), y pasar los campos de las
// subclases a una tabla aparte por subclase (JOINED).
// @Inheritance(strategy = InheritanceType.JOINED)

// Si preferimos usar SINGLE_TABLE. Para que funcione esto, tenemos que agregar una "discriminator column",
// que Hibernate usa para saber, para cada fila, que tipo de subclase es.
// A cada subclase, le damos un @DiscriminatorValue especificando su valor para la columna discrimininador.
// En vez de usar una columna con valores predefinidos, Hibernate nos deja usar una formula SQL para saber
// como diferenciar: @DiscriminatorFormula("CASE WHEN insurance IS NULL THEN 'DOCTOR' ELSE 'PATIENT' END")
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// @DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)

// Si usamos TABLE_PER_CLASS, no necesitamos los discriminadores, y las queries Hibernate las resuelve
// haciendo JOINs de todas las tablas creadas. NOTAR QUE ASÍ NOS CREA LA TABLA inheritance_users!! No
// hicimos User abstract entonces la crea. Si la hacemos abstract, la deja de crear.
// @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
// @Entity
// @Table(name = "inheritance_users")
// public abstract class User {

// Dato: Poner estas tres annotations es lo mismo que usar @MappedSuperclass (User puede ser o no abstract)
// Nota: Los TypedQuery-s, ahora cuando hacemos em.createQuery(..., User.class) nos puede traer instancias de Doctor
// y/o Patient.
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
@Table(name = "users")
public class User {

    @Id // Indico que este atributo es la primary key
    // @GeneratedValue() // Indico que este valor es autogenerado y no debe insertarse
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_user_id_seq")
    @SequenceGenerator(sequenceName = "users_user_id_seq", name = "users_user_id_seq", allocationSize = 1)
    @Column(name = "user_id")
    private Long userId;
    // Esto debe ser Long y no long, porque esto se usa en el dao para saber si el usuario existe o si se debe crear.

    // NOTAR: Por default, para @GeneratedValue de entero, Hibernate crea UN ÚNICO SEQUENCE PARA TODOS, por ende todos
    // los objetos tienen ID único. Esto tiene que ver por cómo se mappea herencia, es la única configuración que
    // asegura que funciona siempre, y por ende lo hicieron el default.
    // Nosotros NO QUEREMOS ESTO, porque ya tenemos datos insertados en las tablas! Entonces no usamos @GeneratedValue
    // solo, sino que le pasamos strategy y generator, y especificamos un @SequenceGenerator. No te olvides el
    // allocationSize porque por default incrementa de a 50!
    // Si queres saber cómo se llama tu sequence ya creado en postgres, abrí el psql y escribí "\ds".

    @Column(length = 100, nullable = false, unique = true)
    // No necesitamos poner un name = "email" en el @Column porque la columna se llama igual que el campo.
    private String email;

    @Column(length = 60, nullable = false)
    private String password;

    // Igual que en Issue agregué los reportedBy y assignedTo, acá puedo (si quiero) agregar el otro lado
    // de la relación. Solo que en vez de @ManyToOne, acá va a ser un @OneToMany.
    // En vez de opción de "optional" acá me da "orphanRemoval", que es "qué queres que haga si elimino un
    // elemento de la lista?" Lo borro de la tabla? O lo dejo vivo?
    // Otro parámetro importante es el mappedBy. Hibernate no tiene forma de saber que esto y lo que pusimos
    // en Issue son dos lados de la misma relación! Entonces lo especificamos con el mappedBy.
    @OneToMany(mappedBy = "reportedBy", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Issue> reportedIssues;

    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Issue> assignedIssues;

    // El FetchType es cuándo se trae los datos. Con EAGER trae las listas de issues cuando carga el usuario, pero con
    // LAZY la trae cuando se hace el get. El problema es que este lazy solo va a funcionar si e get se hace dentro del
    // @Transactional, entonces si vos en tu service traes un User sin los issues cargados, lo retornas, y en el
    // controller intentas acceder a los reportedIssues, no va a poder traerlos!
    // La solución que te hace reprobar es cargarlos forzadamente, en el service o en el dao:
    // Optional<User> user = findById
    // user.ifPresent(u -> u.getReportedIssues().size()); // Fuerzo que se tenga que cargar la colección
    // return user;

    // Para crear una instancia, Hibernate en vez de usar un constructor con los datos nos obliga a poner un
    // constructor default, y luego usa reflection para settear los valores de los campos
    User() {
        // El constructor no necesita ser público, a Hibernate le ancanza con que sea package-private. De todos modos,
        // esto nos impide ponerle el "final" a los campos.
    }

    public User(final String email, final String password) {
        this.userId = null;
        this.email = email;
        this.password = password;
    }

    public User(final Long userId, final String email, final String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Issue> getReportedIssues() {
        return reportedIssues;
    }

    public List<Issue> getAssignedIssues() {
        return assignedIssues;
    }
}
