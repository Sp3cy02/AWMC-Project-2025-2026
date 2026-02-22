package com.gasing.hackhub.controller;

import com.gasing.hackhub.dto.team.request.CreateTeamRequest;
import com.gasing.hackhub.dto.team.request.InviteMemberRequest;
import com.gasing.hackhub.dto.team.response.InviteMemberResponse;
import com.gasing.hackhub.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestBody CreateTeamRequest request) {
        try {
            return ResponseEntity.ok(teamService.createTeam(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteMember(@RequestBody InviteMemberRequest request) {
        try {
            teamService.inviteMember(request);
            return ResponseEntity.ok("Invito inviato con successo!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/respond-invite")
    public ResponseEntity<?> respondToInvite(@RequestBody InviteMemberResponse request) {
        try {
            teamService.rispondiInvito(request);
            String messaggio = request.isAccetta() ? "Benvenuto nel team!" : "Invito rifiutato.";
            return ResponseEntity.ok(messaggio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<?> getMembers(@PathVariable Long teamId) {
        try {
            return ResponseEntity.ok(teamService.getMembersDTO(teamId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //endpoint NON ambiguo (non collide con /{teamId})
    // GET /api/teams/invitations/by-user?userId=123
    @GetMapping("/invitations/by-user")
    public ResponseEntity<?> myInvitations(@RequestParam Long userId) {
        try {
            return ResponseEntity.ok(teamService.getPendingInvitesForUser(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}