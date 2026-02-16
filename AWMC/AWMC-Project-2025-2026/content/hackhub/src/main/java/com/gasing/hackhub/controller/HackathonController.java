package com.gasing.hackhub.controller;

import com.gasing.hackhub.dto.hackathon.HackathonDTO;
import com.gasing.hackhub.model.Hackathon;
import com.gasing.hackhub.repository.HackathonRepository;
import com.gasing.hackhub.service.HackathonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hackathons")
public class HackathonController {

    @Autowired
    private HackathonService hackathonService;

    @Autowired
    private HackathonRepository hackathonRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createHackathon(@RequestBody HackathonDTO request) {
        try {
            // Chiamo il service che fa tutto (crea evento + assegna staff)
            Hackathon newHackathon = hackathonService.createHackathon(request);

            // Restituisco 200 OK e l'oggetto creato
            return ResponseEntity.ok(newHackathon);
        } catch (RuntimeException e) {
            // Se c'è un errore (es. nome duplicato, mentori mancanti), restituisco 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/{id}/next-phase")
    public ResponseEntity<?> advanceHackathonPhase(
            @PathVariable Long id,
            @RequestParam Long organizerId) {
        try {
            hackathonService.advancePhase(id, organizerId);

            // Recupero l'hackathon aggiornato per dire all'utente in che stato siamo finiti
            Hackathon h = hackathonRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hackathon con ID " + id + " non trovato!"));
            return ResponseEntity.ok("Fase avanzata con successo! Nuovo stato: " + h.getStato());

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}