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

    // ISCRIVI UN TEAM A UN HACKATHON
    @PostMapping("/join")
    public ResponseEntity<?> joinHackathon(@RequestBody JoinHackathonRequest request) {
        try {
            hackathonService.registerTeam(request);

            return ResponseEntity.ok("Iscrizione effettuata con successo! Buona fortuna 🍀");
        } catch (RuntimeException e) {
            // Se qualcosa va storto (es. Team già iscritto, Hackathon scaduto)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}