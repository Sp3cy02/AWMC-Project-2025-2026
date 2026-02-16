package com.gasing.hackhub.service;

import com.gasing.hackhub.dto.competizione.CreateEvaluationRequest;
import com.gasing.hackhub.enums.HackathonStatus;
import com.gasing.hackhub.enums.Role;
import com.gasing.hackhub.model.*;
import com.gasing.hackhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvaluationService {

    @Autowired private EvaluationRepository evaluationRepository;

    @Autowired private SubmissionRepository submissionRepository;

    @Autowired private StaffAssignmentRepository staffAssignmentRepository;

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

        // Recupero lo StaffAssignment del Giudice
        StaffAssignment judgeAssignment = staffAssignmentRepository.findByHackathonIdAndUserId(hackathon.getId(), request.getJudgeId())
                .orElseThrow(() -> new RuntimeException("L'utente non fa parte dello staff di questo Hackathon"));

        // Controllo che sia davvero un Giudice
        if (judgeAssignment.getRole() != Role.JUDGE) {
            throw new RuntimeException("L'utente ha un ruolo nello staff, ma non è un Giudice! Ruolo: " + judgeAssignment.getRole());
        }

        // Creo la Valutazione
        Evaluation evaluation = new Evaluation();
        evaluation.setSubmission(submission);
        evaluation.setJudge(judgeAssignment); // Colleghiamo l'incarico, non l'utente grezzo!
        evaluation.setScore(request.getScore());
        evaluation.setComment(request.getComment());

        return evaluationRepository.save(evaluation);
    }
}