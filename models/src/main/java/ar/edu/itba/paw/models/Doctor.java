package ar.edu.itba.paw.models;

import javax.persistence.*;

// @DiscriminatorValue("DOCTOR")
@Entity
public class Doctor extends User {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Specialty specialty;
}
