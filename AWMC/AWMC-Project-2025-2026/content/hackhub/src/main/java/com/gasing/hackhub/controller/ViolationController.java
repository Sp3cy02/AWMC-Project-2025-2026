package com.gasing.hackhub.controller;

import com.gasing.hackhub.dto.staff.ReportViolationRequest;
import com.gasing.hackhub.model.ViolationReport;
import com.gasing.hackhub.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/violations")
public class ViolationController {

    @Autowired
    private ViolationService violationService;

    @PostMapping("/report")
    public ResponseEntity<?> reportViolation(@RequestBody ReportViolationRequest request) {
        try {
            // Chiamo il service passando i dati dal DTO
            ViolationReport report = violationService.segnalaTeam(
                    request.getReporterId(), // ID del Mentore (User ID)
                    request.getHackathonId(),
                    request.getTeamId(),
                    request.getMotivo()
            );
            return ResponseEntity.ok("Segnalazione inviata con successo! ID: " + report.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/decide")
    public ResponseEntity<?> decideViolation(
            @PathVariable Long id,           // ID della segnalazione (ViolationReport)
            @RequestParam Long organizerId,  // Chi sta decidendo (deve essere l'Organizzatore)
            @RequestParam boolean confirm    // true = Squalifica il team, false = Respingi segnalazione
    ) {
        try {
            violationService.gestisciSegnalazione(organizerId, id, confirm);

            String msg = confirm ? "Violazione CONFERMATA. Team Squalificato." : "Segnalazione RESPINTA. Nessuna azione intrapresa.";
            return ResponseEntity.ok(msg);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingReports(@RequestParam Long hackathonId, @RequestParam Long organizerId) {
        try {
            return ResponseEntity.ok(violationService.getPendingReports(hackathonId, organizerId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}