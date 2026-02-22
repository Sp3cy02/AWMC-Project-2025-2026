package com.gasing.hackhub.dto.staff;

import lombok.Data;

@Data
public class AnswerRequest {
    private Long requestId;    // ID della richiesta di supporto
    private Long mentorId;     // Chi risponde

    // Il mentore propone l'orario, il link lo genera l'adapter
    private String dataOra;    // Es. "2026-06-02 15:00"
}