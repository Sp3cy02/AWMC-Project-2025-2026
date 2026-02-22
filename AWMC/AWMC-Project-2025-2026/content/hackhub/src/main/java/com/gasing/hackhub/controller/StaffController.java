package com.gasing.hackhub.controller;

import com.gasing.hackhub.dto.staff.AddMentorRequest;
import com.gasing.hackhub.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @PostMapping("/add")
    public ResponseEntity<?> addMentor(@RequestBody AddMentorRequest request) {
        try {
            staffService.addMentor(request);
            return ResponseEntity.ok("Membro dello staff aggiunto con successo!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Visualizza lo staff dell'hackathon
    @GetMapping("/hackathon/{hackathonId}")
    public ResponseEntity<?> getStaffByHackathon(@PathVariable Long hackathonId) {
        return ResponseEntity.ok(staffService.getStaffByHackathon(hackathonId));
    }
}