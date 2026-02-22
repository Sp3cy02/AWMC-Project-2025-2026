package com.gasing.hackhub.service;

import com.gasing.hackhub.enums.Role;
import com.gasing.hackhub.model.StaffAssignment;
import com.gasing.hackhub.model.Team;
import com.gasing.hackhub.model.ViolationReport;
import com.gasing.hackhub.repository.StaffAssignmentRepository;
import com.gasing.hackhub.repository.TeamRepository;
import com.gasing.hackhub.repository.ViolationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ViolationService {

    @Autowired private ViolationRepository violationRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private StaffAssignmentRepository staffAssignmentRepository;

    @Autowired private ValidatorService validatorService;

    // Mentore che segnala
    @Transactional
    public ViolationReport segnalaTeam(Long mentorId, Long hackathonId, Long teamId, String motivo) {

        // Recupero lo StaffAssignment del mentore (serve per salvarlo come reporter)
        StaffAssignment mentor = staffAssignmentRepository.findByHackathonIdAndUserId(hackathonId, mentorId)
                .orElseThrow(() -> new RuntimeException("Errore: Non fai parte dello staff di questo evento!"));

        // Controllo ruolo centralizzato
        validatorService.requireStaffRole(hackathonId, mentorId, Role.MENTOR);

        // Recupero il Team
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team non trovato"));

        // Creo la segnalazione
        ViolationReport report = new ViolationReport();
        report.setReporter(mentor);   // Chi ha fatto la segnalazione
        report.setReportedTeam(team); // Chi è stato segnalato
        report.setMotivo(motivo);
        report.setGestita(false);     // Nasce "da gestire"

        return violationRepository.save(report);
    }

    // Per l'organizzatore a cui arriva la segnalazione
    @Transactional
    public void gestisciSegnalazione(Long organizerId, Long violationId, boolean confermaSqualifica) {

        // Recupero la segnalazione
        ViolationReport report = violationRepository.findById(violationId)
                .orElseThrow(() -> new RuntimeException("Segnalazione non trovata"));

        // Risalgo all'hackathon tramite il reporter della segnalazione
        Long hackathonId = report.getReporter().getHackathon().getId();

        // Controllo ruolo centralizzato (organizer su quell'hackathon)
        validatorService.requireStaffRole(hackathonId, organizerId, Role.ORGANIZER);

        // Controllo se è già stata gestita per non farlo due volte
        if (report.isGestita()) {
            throw new RuntimeException("Questa segnalazione è già stata chiusa!");
        }

        if (confermaSqualifica) {
            // Caso: Squalifica
            Team team = report.getReportedTeam();
            team.setDisqualified(true);
            teamRepository.save(team);
        }
        // Caso: niente squalifica, chiudo solo il report.

        // Chiudo la segnalazione
        report.setGestita(true);
        violationRepository.save(report);
    }

    // Per la dashboard dell'organizzatore
    public List<ViolationReport> getPendingReports(Long hackathonId, Long organizerId) {

        // Controllo ruolo centralizzato
        validatorService.requireStaffRole(hackathonId, organizerId, Role.ORGANIZER);

        // Il DB restituisce solo le righe giuste
        return violationRepository.findByGestitaFalseAndReporter_Hackathon_Id(hackathonId);
    }
}
