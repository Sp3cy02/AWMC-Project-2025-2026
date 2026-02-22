package com.gasing.hackhub.dto.staff;

import lombok.Data;

@Data
public class ReportViolationRequest {
    private Long reporterId;    // ID dell'Utente (Mentore) che fa la segnalazione
    private Long hackathonId;   // ID dell'evento (per verificare che sia staff lì)
    private Long teamId;        // ID del Team segnalato
    private String motivo;
}