package com.gasing.hackhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@Table(name = "Utente") // user non si può usare in SQL per nominare una tabella
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id si autoincrementa nel database
    private Long id;

    @Column(nullable = false, unique=true) // unique fa si che non si possa usare l'email per due utenti
    private String email;

    @Column(nullable = false)
    private String passwordHash; // la password non deve esser emostrata in chiaro

    @Column(nullable = false) // non accetta valori vuoti
    private String nome;

    @Column(nullable = false)
    private String cognome;

    // Relazioni

    @OneToMany(mappedBy = "user") // serve per il collegamento di un utente al suo ruolo staff che può avere in vari hackathon
    @ToString.Exclude
    @JsonIgnore
    private List<StaffAssignment> assignments;

    @ManyToOne // tanti utenti fanno parte di un team
    @JoinColumn(name = "team_id") // crea una colonna team_id nella tabella Utente
    @ToString.Exclude  // protegge debugger e log errori
    @JsonIgnore // non serve se usiamo i DTO, però lo lascio per sicurezza nei test
    private Team team;
}