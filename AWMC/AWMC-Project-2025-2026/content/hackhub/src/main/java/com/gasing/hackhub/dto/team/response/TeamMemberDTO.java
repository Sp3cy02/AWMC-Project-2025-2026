package com.gasing.hackhub.dto.team.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamMemberDTO {
    private Long id;
    private String nome;
    private String cognome;
    private String email;
}