package com.gasing.hackhub.controller;

import com.gasing.hackhub.dto.competizione.SubmitProjectRequest;
import com.gasing.hackhub.model.Submission;
import com.gasing.hackhub.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    // Invio o Aggiorno un progetto
    @PostMapping("/submit")
    public ResponseEntity<?> submitProject(@RequestBody SubmitProjectRequest request) {
        try {
            // Chiamo il service che gestisce la logica (Crea o Aggiorna)
            Submission submission = submissionService.submitProject(request);

            return ResponseEntity.ok(submission);
        } catch (RuntimeException e) {
            // Se c'è un errore (es. non sei nel team, hackathon scaduto)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}