package com.gasing.hackhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import com.gasing.hackhub.enums.Role;


@Entity
@Table(name = "staff_assignment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // --- RELAZIONE CON USER ---
    @ManyToOne(optional = false) // optional=false: Non può esistere un incarico senza una persona fisica
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude  // evita il loop di stampa con User
    @JsonIgnore        // evita il loop JSON
    private User user;

    // --- RELAZIONE CON HACKATHON ---
    @ManyToOne(optional = false) // optional=false: Non può esistere un incarico senza un evento
    @JoinColumn(name = "hackathon_id", nullable = false)
    @ToString.Exclude  // evita il loop di stampa con Hackathon
    @JsonIgnore        // evita il loop JSON
    private Hackathon hackathon;
}
