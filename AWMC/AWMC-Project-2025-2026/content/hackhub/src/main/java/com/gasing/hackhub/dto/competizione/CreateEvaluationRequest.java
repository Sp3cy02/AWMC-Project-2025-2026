package com.gasing.hackhub.dto.competizione;

import lombok.Data;

@Data
public class CreateEvaluationRequest {

    private Long submissionId;
    private int score;
    private Long judgeId;
    private String comment;
}
