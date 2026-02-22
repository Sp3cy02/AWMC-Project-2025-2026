package com.gasing.hackhub.controller;

import com.gasing.hackhub.dto.staff.AnswerRequest;
import com.gasing.hackhub.dto.staff.CreateSupportRequest;
import com.gasing.hackhub.model.SupportRequest;
import com.gasing.hackhub.service.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    @Autowired
    private SupportService supportService;

    // Il Team chiede aiuto
    @PostMapping("/create")
    public ResponseEntity<?> createRequest(@RequestBody CreateSupportRequest request) {
        try {
            SupportRequest newRequest = supportService.createRequest(request);
            return ResponseEntity.ok(newRequest);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resolve")
    public ResponseEntity<?> resolveRequest(@RequestBody AnswerRequest request) {
        try {
            // Passiamo dataOra al service, che userà l'Adapter per generare il link
            SupportRequest resolvedRequest = supportService.resolveRequest(
                    request.getRequestId(),
                    request.getMentorId(),
                    request.getDataOra()
            );
            return ResponseEntity.ok("Richiesta risolta! Call prenotata su: " + resolvedRequest.getCallLink());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getTeamRequests(@PathVariable Long teamId) {
        List<SupportRequest> requests = supportService.getRequestsByTeam(teamId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/mentor/{userId}")
    public ResponseEntity<?> getMentorRequests(@PathVariable Long userId) {
        List<SupportRequest> requests = supportService.getRequestsByMentor(userId);
        return ResponseEntity.ok(requests);
    }
}
