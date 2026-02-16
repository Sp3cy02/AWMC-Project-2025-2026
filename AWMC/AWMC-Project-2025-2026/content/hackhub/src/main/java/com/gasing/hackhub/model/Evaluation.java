package com.gasing.hackhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "evaluation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int score; // Punteggio numerico compreso tra 0 e 10

    @Column(nullable = false, length = 1000)
    private String comment;


    @OneToOne(optional = false)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    @ToString.Exclude
    @JsonIgnore
    private Submission submission;


    @ManyToOne(optional = false)
    @JoinColumn(name = "judge_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private StaffAssignment judge; // Colleghiamo lo StaffAssignment per essere sicuri che sia uno staff di quell'hackathon
}