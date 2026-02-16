package com.gasing.hackhub.dto.team.response;

import lombok.Data;

@Data
public class InviteMemberResponse {

    private Long teamId; // Il team a cui voglio rispondere
    private Long userId; // Io che rispondo
    private boolean accetta; // true = accetto, false = rifiuto
}
