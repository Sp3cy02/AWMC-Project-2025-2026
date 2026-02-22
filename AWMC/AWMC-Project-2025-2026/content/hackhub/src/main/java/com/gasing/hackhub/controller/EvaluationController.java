package com.gasing.hackhub.controller;

import com.gasing.hackhub.dto.competizione.CreateEvaluationRequest;
import com.gasing.hackhub.model.Evaluation;
import com.gasing.hackhub.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    // Crea una valutazione
    @PostMapping("/create")
    public ResponseEntity<?> createEvaluation(@RequestBody CreateEvaluationRequest request) {
        try {
            // Chiamo il service che controlla se l'utente è judge e salva il voto
            Evaluation savedEvaluation = evaluationService.createEvaluation(request);

            return ResponseEntity.ok("Valutazione salvata con successo! Voto: " + savedEvaluation.getScore());
        } catch (RuntimeException e) {
            // Se c'è un errore (es. Non sei un giudice, Submission non trovata, Già votato)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}