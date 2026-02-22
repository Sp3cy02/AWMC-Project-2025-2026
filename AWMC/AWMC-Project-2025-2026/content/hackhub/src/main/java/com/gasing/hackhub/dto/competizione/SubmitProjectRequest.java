package com.gasing.hackhub.dto.competizione;

import lombok.Data;

@Data
public class SubmitProjectRequest {

    private Long hackathonId;
    private Long teamId;
    private Long userId; // L'utente che preme il bottone "Invia"

    // I dati veri e propri del progetto
    private String repositoryLink; // Es. github
    private String descrizione;
}