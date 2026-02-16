package com.gasing.hackhub.dto.team.request;

import lombok.Data;

@Data
public class JoinHackathonRequest {
    private Long hackathonId; // A quale hackathon ci iscriviamo?
    private Long teamId;      // Quale team iscriviamo?
    private Long userId;      // Chi sta facendo l'iscrizione?
}