package com.gasing.hackhub.dto.team.request;

import lombok.Data;

@Data
public class JoinHackathonRequest {
    private Long hackathonId;
    private Long teamId;
    private Long userId;
}