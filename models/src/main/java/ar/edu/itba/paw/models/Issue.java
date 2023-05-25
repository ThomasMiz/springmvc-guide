package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "issues_issue_id_seq")
    @SequenceGenerator(sequenceName = "issues_issue_id_seq", name = "issues_issue_id_seq", allocationSize = 1)
    @Column(name = "issue_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    // El "reportedBy" es una relación de cardinalidad N:1. Esto se hace con un @ManyToOne
    // OJO QUE ES DISTINTO QUE EL @OneToMany!!
    // @ManyToOne(targetEntity = User.class) // Puedo especificar la clase target, pero en este caso no
    // necesitamos. Útil cando tenes un set, o en vez de un User quiero una clase que extiende de User.
    // Otras opciones son "cascade", las reglas de cascadeo y "fetch", un tema de cómo implementa la query.
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private User reportedBy;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    private User assignedTo;

    @Enumerated(EnumType.ORDINAL)
    private Priority priority;

    Issue() {

    }

    public Issue(String title, String description, User reportedBy, User assignedTo, Priority priority) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.reportedBy = reportedBy;
        this.assignedTo = assignedTo;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(User reportedBy) {
        this.reportedBy = reportedBy;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
