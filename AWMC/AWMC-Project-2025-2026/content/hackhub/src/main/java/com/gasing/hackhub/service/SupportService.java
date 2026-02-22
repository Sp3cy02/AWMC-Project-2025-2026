package com.gasing.hackhub.service;

import com.gasing.hackhub.adapter.CalendarGateway;
import com.gasing.hackhub.dto.staff.CreateSupportRequest;
import com.gasing.hackhub.enums.HackathonStatus;
import com.gasing.hackhub.enums.RequestStatus;
import com.gasing.hackhub.enums.Role;
import com.gasing.hackhub.model.*;
import com.gasing.hackhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupportService {

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private StaffAssignmentRepository staffAssignmentRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private CalendarGateway calendarAdapter;

    @Transactional
    public SupportRequest createRequest(CreateSupportRequest dto) {

        // Verifico che il team esista
        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team non trovato"));

        // Controlli
        boolean isActiveInHackathon = registrationRepository.findAll().stream()
                .anyMatch(reg -> reg.getTeam().getId().equals(team.getId()) &&
                        reg.getHackathon().getStato() == HackathonStatus.ONGOING);

        if (!isActiveInHackathon) {
            throw new RuntimeException("Non puoi chiedere supporto! Il team non è iscritto a nessun Hackathon in corso.");
        }

        // Creo la richiesta
        SupportRequest req = new SupportRequest();
        req.setTeam(team);
        req.setProblema(dto.getProblema());
        req.setStatus(RequestStatus.OPEN); // Nasce aperta

        return supportRepository.save(req);
    }

    // Propone Call
    @Transactional
    public SupportRequest resolveRequest(Long requestId, Long mentorUserId, String dataOra) {

        // Recupero la richiesta
        SupportRequest req = supportRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Richiesta non trovata"));

        // DEVO COLLEGARE IL MENTORE ALLA RICHIESTA
        // Cerchiamo direttamente lo StaffAssignment di questo utente.
        // Siccome un utente può essere mentore in 10 eventi, dobbiamo trovare quello giusto.
        // Per ora, assumiamo che il mentore stia agendo dall'hackathon corretto.

        // Recuperiamo tutti i ruoli di questo utente
        List<StaffAssignment> assignments = staffAssignmentRepository.findByUserId(mentorUserId);

        // Filtriamo: cerchiamo un ruolo da MENTOR che sia compatibile col Team
        StaffAssignment validMentorAssignment = null;

        for (StaffAssignment assignment : assignments) {
            if (assignment.getRole() == Role.MENTOR) {
                // Controlliamo se il team è iscritto a questo hackathon
                boolean isTeamRegistered = registrationRepository
                        .findByHackathonAndTeam(assignment.getHackathon(), req.getTeam())
                        .isPresent();

                if (isTeamRegistered) {
                    validMentorAssignment = assignment;
                    break; // Trovato!
                }
            }
        }

        if (validMentorAssignment == null) {
            throw new RuntimeException("Non sei un Mentore per l'hackathon a cui partecipa questo team!");
        }

        // Chiediamo al sistema esterno di generare il link per l'orario scelto
        String nomeMentore = validMentorAssignment.getUser().getNome();
        String nomeTeam = req.getTeam().getNome();

        String linkGenerato = calendarAdapter.prenotaCall(nomeMentore, nomeTeam, dataOra);


        // Aggiorno la richiesta nel DB
        req.setMentor(validMentorAssignment);
        req.setCallLink(linkGenerato); // Salvo il link restituito dall'adapter
        req.setStatus(RequestStatus.RESOLVED);

        return supportRepository.save(req);
    }

    // Metodo per mostrare le richieste al team
    public List<SupportRequest> getRequestsByTeam(Long teamId) {
        return supportRepository.findByTeamId(teamId);
    }

    // Metodo per mostrarle al mentore
    public List<SupportRequest> getRequestsByMentor(Long userId) {
        return supportRepository.findByMentor_User_Id(userId);
    }
}