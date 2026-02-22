package com.gasing.hackhub.controller;

import com.gasing.hackhub.dto.hackathon.HackathonDTO;
import com.gasing.hackhub.dto.hackathon.ProclaimWinnerRequest;
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
            // Chiamo il service che crea evento + assegna staff
            Hackathon newHackathon = hackathonService.createHackathon(request);

            // Restituisco 200 OK e l'oggetto creato
            return ResponseEntity.ok(newHackathon);
        } catch (RuntimeException e) {
            // Se c'è un errore (es. nome duplicato, mentori mancanti), restituisco 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @GetMapping
    public ResponseEntity<?> getAllHackathons() {
        return ResponseEntity.ok(hackathonService.getAllHackathons());
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getHackathonDetail(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(hackathonService.getHackathonById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/{id}/next-phase")
    public ResponseEntity<?> advanceHackathonPhase(@PathVariable Long id, @RequestParam Long organizerId) {
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

    @PostMapping("/{id}/pay-prize")
    public ResponseEntity<?> payPrize(@PathVariable Long id, @RequestParam Long organizerId) {
        try {
            // Chiama il metodo specifico che usa l'Adapter
            String risultato = hackathonService.erogaPremio(id, organizerId);
            return ResponseEntity.ok(risultato);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/proclaim-winner")
    public ResponseEntity<?> proclaimWinner(@RequestBody ProclaimWinnerRequest request) {
        try {
            hackathonService.proclaimWinner(request);
            return ResponseEntity.ok("Vincitore proclamato con successo! L'Hackathon è ora concluso.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/teams")
    public ResponseEntity<?> getRegisteredTeams(@PathVariable Long id) {
    try {
        return ResponseEntity.ok(hackathonService.getRegisteredTeams(id));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    }
}