package com.gasing.hackhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link al repository (GitHub/GitLab) - Obbligatorio
    @Column(nullable = false)
    private String repositoryLink;


    // Una descrizione testuale del progetto
    @Column(length = 2000)
    private String descrizione;

    // Data e ora esatta della consegna (importante per vedere se hanno consegnato in ritardo!)
    @Column(nullable = false)
    private LocalDateTime dataInvio;

    @OneToOne(optional = false) // una submission appartiene a una specifica iscrizione del team all'hackathon
    @JoinColumn(name = "registration_id", nullable = false, unique = true)
    @ToString.Exclude // Protegge i log
    @JsonIgnore       // Protegge il JSON
    private HackathonRegistration registration;

    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL) // una valutazione appartiene a una specifica submission del team all'hackathon
    @ToString.Exclude
    @JsonIgnore
    private Evaluation evaluation;
}