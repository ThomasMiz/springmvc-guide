package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.interfaces.services.IssueService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Issue;
import ar.edu.itba.paw.models.Priority;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IssueServiceImpl implements IssueService {

    private final UserService userService;

    @Autowired
    public IssueServiceImpl(final UserService userService) {
        this.userService = userService;
    }

    @Transactional
    @Override
    public Issue reportIssue(String userEmail, String title, String description, Priority priority) {
        final User user = userService.findByEmail(userEmail).orElseThrow(UserNotFoundException::new);

        // Pero cómo se está insertando este issue?? Si yo ni siquiera tengo un IssueDao??
        // Es porque en el UserDaoJpa pusimos un @PersistenceContext en el EntityManager! Esto guarda un estado que
        // le permite a Hibernate hacer mucha magia como esta. También le permite que si yo pido la misma entidad
        // varias veces, en vez de hacer la misma query varias veces te retorna siempre la misma.
        // Se acuerda "Cuáles son las entidades con las que estoy trabajando ahora mismo?". Pero qué es "Ahora mismo?"
        // Es el contexto transaccional! Una vez que termina la transacción del @Transactional, Hibernate se fija si
        // alguna de estas entidades fueron manipuladas. Si cambias, por ejemplo, el mail y/o password de un User,
        // entonces actualiza la DB y se asegura que refleje este estado. Para que pase esto, en el @OneToMany del User
        // tuvimos que poner el cascade = CascadeType.ALL, sino no funciona!
        final Issue issue = new Issue(title, description, user, null, priority);
        user.getReportedIssues().add(issue);

        return issue;
    }
}
