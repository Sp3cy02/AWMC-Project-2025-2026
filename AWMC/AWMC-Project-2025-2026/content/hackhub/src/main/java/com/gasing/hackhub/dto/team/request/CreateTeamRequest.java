package com.gasing.hackhub.dto.team.request;

import lombok.Data;

@Data
public class CreateTeamRequest {

    private String nomeTeam;
    private Long creatorUserId;
}
