package com.gasing.hackhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gasing.hackhub.enums.RequestStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String problema;  // Descrizione del problema

    @Column // Il link viene inserito dopo dal mentore, quindi può essere null all'inizio
    private String callLink;  // Link alla videochiamata (Google Meet/Zoom)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.OPEN;

    // Relazioni

    @ManyToOne(optional = false) // Una richiesta deve per forza appartenere a un team
    @JoinColumn(name = "team_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Team team;

    @ManyToOne // Il mentore è opzionale all'inizio (chiunque può prenderla in carico)
    @JoinColumn(name = "mentor_id") // Colleghiamo allo StaffAssignment (non all'utente diretto)
    @ToString.Exclude
    @JsonIgnore
    private StaffAssignment mentor;
}