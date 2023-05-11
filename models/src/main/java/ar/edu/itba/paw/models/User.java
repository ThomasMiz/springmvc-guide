package ar.edu.itba.paw.models;

import javax.persistence.*;

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
}
