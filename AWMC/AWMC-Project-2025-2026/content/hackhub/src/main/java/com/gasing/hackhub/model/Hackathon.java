package com.gasing.hackhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gasing.hackhub.enums.HackathonStatus;


@Entity
@Table(name = "hackathon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hackathon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 5000)
    private String regolamento;

    @Column(nullable = false)
    private String luogo;

    private Double premio;

    @Column(nullable = false)
    private int dimensioneMassimaTeam;

    @Column(nullable = false)
    private LocalDateTime dataInizio;

    @Column(nullable = false)
    private LocalDateTime dataFine;

    @Column(nullable = false)
    private LocalDateTime scadenzaIscrizione;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HackathonStatus stato;

    // Cascade ALL: Se cancello l'evento, cancello anche l'assegnazione dei giudici.
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Protegge i log dal loop infinito
    @JsonIgnore       // Protegge il JSON
    private List<StaffAssignment> staff = new ArrayList<>();

    // Cascade ALL: Se cancello l'evento, cancello tutte le iscrizioni.
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Protegge i log dal loop infinito
    @JsonIgnore       // Protegge il JSON
    private List<HackathonRegistration> registrations = new ArrayList<>();
}
