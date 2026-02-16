package com.gasing.hackhub.dto.hackathon;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HackathonDTO {

    // Ci serve per renderlo Organizzatore
    private Long organizerId;

    // Dati dell'evento
    private String nome;
    private String regolamento; // Testo lungo
    private String luogo;
    private Double premio;
    private int dimensioneMassimaTeam;

    // Staff dell'evento
    private Long judgeId;           // L'ID dell'utente che farà da Giudice
    private List<Long> mentorIds;   // Lista di ID per i Mentori (es. [2, 3])

    // LocalDateTime per includere l'orario
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private LocalDateTime scadenzaIscrizione;
}