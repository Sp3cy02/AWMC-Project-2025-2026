package com.gasing.hackhub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class ViolationReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String motivo; // Descrizione della violazione

    // False = Aperta (da vedere), true = Chiusa (gestita dall'organizzatore)
    @Column(nullable = false)
    private boolean gestita = false;

    // Data della segnalazione (utile per l'ordine cronologico)
    private LocalDateTime dataSegnalazione = LocalDateTime.now();

    // Relazioni

    @ManyToOne(optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore // Evitiamo loop JSON
    private StaffAssignment reporter;


    @ManyToOne(optional = false)
    @JoinColumn(name = "reported_team_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Team reportedTeam;
}