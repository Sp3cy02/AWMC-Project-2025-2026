package com.gasing.hackhub.controller;

import com.gasing.hackhub.dto.team.request.JoinHackathonRequest;
import com.gasing.hackhub.service.HackathonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    @Autowired
    private HackathonService hackathonService;

    // Iscrivere il team a un hackathon
    @PostMapping("/join")
    public ResponseEntity<?> joinHackathon(@RequestBody JoinHackathonRequest request) {
        try {
            hackathonService.registerTeam(request);
            return ResponseEntity.ok("Iscrizione effettuata con successo! Buona fortuna!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Leggere i team iscritti a un hackathon (NO RegistrationService)
    @GetMapping("/hackathon/{hackathonId}/teams")
    public ResponseEntity<?> getTeamsByHackathon(@PathVariable Long hackathonId) {
        try {
            return ResponseEntity.ok(hackathonService.getRegisteredTeams(hackathonId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}