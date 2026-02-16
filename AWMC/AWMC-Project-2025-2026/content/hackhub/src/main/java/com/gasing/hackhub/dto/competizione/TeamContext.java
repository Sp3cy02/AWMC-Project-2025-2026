package com.gasing.hackhub.dto.competizione;

import com.gasing.hackhub.model.Hackathon;
import com.gasing.hackhub.model.Team;
import com.gasing.hackhub.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamContext {
    private Hackathon hackathon;
    private Team team;
    private User user;
}