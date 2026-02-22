package com.gasing.hackhub.service;

import com.gasing.hackhub.dto.staff.AddMentorRequest;
import com.gasing.hackhub.dto.staff.ReportViolationRequest;
import com.gasing.hackhub.enums.Role;
import com.gasing.hackhub.model.*;
import com.gasing.hackhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffService {

    @Autowired private StaffAssignmentRepository staffAssignmentRepository;

    @Autowired private HackathonRepository hackathonRepository;

    @Autowired private UserRepository userRepository;

    @Autowired private ValidatorService validatorService;

    @Transactional
    public void addMentor(AddMentorRequest request) {

        if (request.getRuolo() != Role.MENTOR) {
            throw new RuntimeException("Operazione non consentita: Dopo la creazione dell'Hackathon è possibile aggiungere solo nuovi MENTORI (non Giudici o Organizzatori).");
        }

        // Controllo Esistenza Hackathon
        Hackathon hackathon = hackathonRepository.findById(request.getHackathonId())
                .orElseThrow(() -> new RuntimeException("Hackathon non trovato"));

        // Controllo Permessi (Solo l'Organizzatore di questo hackathon può aggiungere staff)
        validatorService.requireStaffRole(request.getHackathonId(), request.getOrganizerId(), Role.ORGANIZER);


        // Recupero l'utente da aggiungere (tramite email)
        User newStaffUser = userRepository.findByEmail(request.getEmailUtente())
                .orElseThrow(() -> new RuntimeException("Utente con email " + request.getEmailUtente() + " non trovato."));

        // Controllo se è già nello staff
        boolean alreadyInStaff = staffAssignmentRepository
                .findByHackathonIdAndUserId(request.getHackathonId(), newStaffUser.getId())
                .isPresent();

        if (alreadyInStaff) {
            throw new RuntimeException("L'utente è già membro dello staff per questo Hackathon!");
        }

        // Salvo
        StaffAssignment newAssignment = new StaffAssignment();
        newAssignment.setHackathon(hackathon);
        newAssignment.setUser(newStaffUser);
        newAssignment.setRole(request.getRuolo()); // Sarà sicuramente MENTOR grazie al controllo iniziale

        staffAssignmentRepository.save(newAssignment);
    }


    public List<StaffAssignment> getStaffByHackathon(Long hackathonId) {
        return staffAssignmentRepository.findByHackathonId(hackathonId);
    }
}