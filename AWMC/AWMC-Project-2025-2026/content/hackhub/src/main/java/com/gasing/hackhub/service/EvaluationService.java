package com.gasing.hackhub.service;

import com.gasing.hackhub.dto.competizione.CreateEvaluationRequest;
import com.gasing.hackhub.enums.HackathonStatus;
import com.gasing.hackhub.enums.Role;
import com.gasing.hackhub.model.Evaluation;
import com.gasing.hackhub.model.Hackathon;
import com.gasing.hackhub.model.StaffAssignment;
import com.gasing.hackhub.model.Submission;
import com.gasing.hackhub.repository.EvaluationRepository;
import com.gasing.hackhub.repository.StaffAssignmentRepository;
import com.gasing.hackhub.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvaluationService {

    @Autowired private EvaluationRepository evaluationRepository;
    @Autowired private SubmissionRepository submissionRepository;
    @Autowired private StaffAssignmentRepository staffAssignmentRepository;

    @Autowired private ValidatorService validatorService;

    @Transactional
    public Evaluation createEvaluation(CreateEvaluationRequest request) {

        // Recupero il progetto da valutare
        Submission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new RuntimeException("Submission non trovata"));

        // Controllo se è già stata valutata
        if (evaluationRepository.existsBySubmission(submission)) {
            throw new RuntimeException("Questa submission è già stata valutata!");
        }

        // Risalgo all'Hackathon per contesto
        Hackathon hackathon = submission.getRegistration().getHackathon();

        // Controllo lo stato dell'Hackathon
        if (hackathon.getStato() != HackathonStatus.EVALUATION) {
            throw new RuntimeException("Le votazioni non sono aperte. L'Hackathon è in fase: " + hackathon.getStato());
        }

        // Recupero lo StaffAssignment del Giudice (serve come riferimento per salvarlo in Evaluation)
        StaffAssignment judgeAssignment = staffAssignmentRepository
                .findByHackathonIdAndUserId(hackathon.getId(), request.getJudgeId())
                .orElseThrow(() -> new RuntimeException("L'utente non fa parte dello staff di questo Hackathon"));

        // Controllo che sia davvero un Giudice (centralizzato)
        validatorService.requireStaffRole(hackathon.getId(), request.getJudgeId(), Role.JUDGE);

        // Creo la Valutazione
        Evaluation evaluation = new Evaluation();
        evaluation.setSubmission(submission);
        evaluation.setJudge(judgeAssignment); // Colleghiamo l'incarico, non l'utente grezzo!
        evaluation.setScore(request.getScore());
        evaluation.setComment(request.getComment());

        return evaluationRepository.save(evaluation);
    }
}
