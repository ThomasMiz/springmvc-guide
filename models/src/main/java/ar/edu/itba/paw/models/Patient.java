package ar.edu.itba.paw.models;

import javax.persistence.*;

// @DiscriminatorValue("PATIENT")
@Entity
public class Patient extends User {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InsuranceCompany insurance;
}
