package com.gasing.hackhub.dto.hackathon;

import lombok.Data;

@Data
public class ProclaimWinnerRequest {
    private Long hackathonId;
    private Long organizerId; // Per verificare che sia lui a fare l'azione
    private Long winningTeamId; // L'ID del team che ha scelto

}
