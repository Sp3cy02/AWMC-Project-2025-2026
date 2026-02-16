package com.gasing.hackhub.dto.team.request;

import lombok.Data;

@Data
public class InviteMemberRequest {

    private Long teamId;        // Chi invita
    private String emailUtente; // Chi viene invitato (usiamo l'email che è più comoda dell'ID)
}
