package com.gasing.hackhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "hackathon_registration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HackathonRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataRegistrazione;

    @Column(nullable = false)
    private boolean winner = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id", nullable = false)   // RELAZIONE CON IL TEAM
    @ToString.Exclude // Protegge i log
    @JsonIgnore       // Protegge il JSON
    private Team team;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false)  // RELAZIONE CON L'HACKATHON
    @ToString.Exclude // Protegge i log
    @JsonIgnore       // Protegge il JSON
    private Hackathon hackathon;

    @OneToOne(mappedBy = "registration", cascade = CascadeType.ALL) // RELAZIONE CON LA SUBMISSION (Il Progetto)
    @ToString.Exclude    // mappedBy = "registration": significa che la chiave esterna sarà nella tabella Submission
    @JsonIgnore
    private Submission submission;

}

