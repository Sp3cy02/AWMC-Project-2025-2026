package com.gasing.hackhub.controller;

import com.gasing.hackhub.dto.team.request.CreateTeamRequest;
import com.gasing.hackhub.dto.team.request.InviteMemberRequest;
import com.gasing.hackhub.dto.team.response.InviteMemberResponse;
import com.gasing.hackhub.model.Team;
import com.gasing.hackhub.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// rest controller dice a spring che questa classe risponde alle chiamate web
// request mapping definisce l'indirizzo base per tutte le chiamate qui dentro
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    // collego il service che è quello che fa il lavoro sporco
    @Autowired
    private TeamService teamService;

    // --- 1. CREA UN NUOVO TEAM ---
    // url completo: POST http://localhost:8080/api/teams/create
    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestBody CreateTeamRequest request) {
        try {
            // passo la richiesta al service
            Team newTeam = teamService.createTeam(request);
            // se va tutto bene restituisco 200 ok con il team creato
            return ResponseEntity.ok(newTeam);
        } catch (RuntimeException e) {
            // se c'è un errore (es. nome duplicato) restituisco 400 bad request col messaggio
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- 2. INVITA UN MEMBRO ---
    // url completo: POST http://localhost:8080/api/teams/invite
    @PostMapping("/invite")
    public ResponseEntity<?> inviteMember(@RequestBody InviteMemberRequest request) {
        try {
            // dico al service di mandare l'invito
            teamService.inviteMember(request);
            // restituisco un messaggio semplice per dire che è andata
            return ResponseEntity.ok("Invito inviato con successo!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- 3. RISPONDI ALL'INVITO (ACCETTA/RIFIUTA) ---
    // url completo: POST http://localhost:8080/api/teams/respond-invite
    @PostMapping("/respond-invite")
    public ResponseEntity<?> respondToInvite(@RequestBody InviteMemberResponse request) {
        try {
            // passo la risposta dell'utente al service
            teamService.rispondiInvito(request);

            String messaggio = request.isAccetta() ? "Benvenuto nel team!" : "Invito rifiutato.";
            return ResponseEntity.ok(messaggio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}