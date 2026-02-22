package com.gasing.hackhub.dto.team.response;

import com.gasing.hackhub.enums.InviteStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PendingInviteDTO {
    private Long invitationId;
    private Long teamId;
    private String teamNome;
    private InviteStatus status;
}