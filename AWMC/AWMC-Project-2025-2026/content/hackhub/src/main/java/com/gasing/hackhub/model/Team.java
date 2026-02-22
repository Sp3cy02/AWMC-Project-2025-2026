package com.gasing.hackhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private boolean isDisqualified = false;

    @OneToMany(mappedBy = "team") // cerca la variabile 'team' dentro la classe User.
    @ToString.Exclude  // serve per non far dare errore nei log o debugger
    @JsonIgnore // non serve se usiamo i DTO ma lascio per sicurezza
    private List<User> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)    // CascadeType.ALL significa: se cancello il team, cancello anche i suoi inviti.
    @ToString.Exclude
    @JsonIgnore
    private List<TeamInvitation> inviti = new ArrayList<>();     // Serve per tracciare gli inviti spediti dal team.
}