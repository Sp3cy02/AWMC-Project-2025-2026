package com.gasing.hackhub.service;

import com.gasing.hackhub.dto.competizione.TeamContext;
import com.gasing.hackhub.dto.hackathon.HackathonDTO;
import com.gasing.hackhub.enums.HackathonStatus;
import com.gasing.hackhub.enums.Role;
import com.gasing.hackhub.model.*;
import com.gasing.hackhub.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HackathonService {

    @Autowired
    private HackathonRepository hackathonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffAssignmentRepository staffAssignmentRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ValidatorService validatorService;

    // CREAZIONE HACKATHON

    @Transactional
    public Hackathon createHackathon(HackathonDTO request) {

        // VALIDAZIONI PRELIMINARI
        if (hackathonRepository.existsByNome(request.getNome())) {
            throw new RuntimeException("Esiste già un hackathon con questo nome!");
        }

        // Controllo che ci sia almeno un mentore (come da specifiche "uno o più")
        if (request.getMentorIds() == null || request.getMentorIds().isEmpty()) {
            throw new RuntimeException("Devi assegnare almeno un Mentore!");
        }

        // RECUPERO GLI UTENTI (Staff)

        // L'Organizzatore
        User organizerUser = userRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("Organizzatore non trovato"));

        // Il Giudice
        User judgeUser = userRepository.findById(request.getJudgeId())
                .orElseThrow(() -> new RuntimeException("Giudice non trovato"));

        // CREO E SALVO L'HACKATHON
        Hackathon hackathon = new Hackathon();
        hackathon.setNome(request.getNome());
        hackathon.setRegolamento(request.getRegolamento());
        hackathon.setLuogo(request.getLuogo());
        hackathon.setPremio(request.getPremio());
        hackathon.setDimensioneMassimaTeam(request.getDimensioneMassimaTeam());

        hackathon.setDataInizio(request.getDataInizio());
        hackathon.setDataFine(request.getDataFine());
        hackathon.setScadenzaIscrizione(request.getScadenzaIscrizione());

        hackathon.setStato(HackathonStatus.REGISTRATION_OPEN);

        // Salvo per ottenere l'ID
        hackathon = hackathonRepository.save(hackathon);

        // ASSEGNAZIONE RUOLI (Salvo nella tabella StaffAssignment)

        // Assegno l'Organizzatore
        assignRole(organizerUser, hackathon, Role.ORGANIZER);

        // Assegno il Giudice
        assignRole(judgeUser, hackathon, Role.JUDGE);

        // Assegno i Mentori (Ciclo sulla lista degli ID)
        for (Long mentorId : request.getMentorIds()) {
            User mentorUser = userRepository.findById(mentorId)
                    .orElseThrow(() -> new RuntimeException("Mentore con ID " + mentorId + " non trovato"));

            assignRole(mentorUser, hackathon, Role.MENTOR);
        }

        return hackathon;
    }

    // CAMBIARE LO STATO DELL?HACKATHON

    @Transactional
    public void advancePhase(Long hackathonId, Long organizerId) {

        // Recupero Hackathon
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon non trovato"));

        // Controllo Sicurezza
        StaffAssignment assignment = staffAssignmentRepository.findByHackathonIdAndUserId(hackathonId, organizerId)
                .orElseThrow(() -> new RuntimeException("Non fai parte dello staff!"));

        if (assignment.getRole() != Role.ORGANIZER) {
            throw new RuntimeException("Solo l'Organizzatore può cambiare la fase dell'evento!");
        }

        switch (hackathon.getStato()) {
            case REGISTRATION_OPEN:
                hackathon.setStato(HackathonStatus.ONGOING);
                // Qui possiamo settare dataInizio = NOW()
                break;

            case ONGOING:
                hackathon.setStato(HackathonStatus.EVALUATION);
                // Qui possiamo settare dataFine = NOW()
                break;

            case EVALUATION:
                calcolaVincitore(hackathon);
                // Se è tutto ok si chiude altrimenti lancia eccezione
                hackathon.setStato(HackathonStatus.CONCLUDED);
                break;

            case CONCLUDED:
                throw new RuntimeException("L'evento è già concluso! Non puoi andare oltre.");

            default:
                throw new RuntimeException("Stato non valido.");
        }

        // Salvo
        hackathonRepository.save(hackathon);
    }

    // ISCRIZIONE DEL TEAM ALL?HACKATHON

    @Transactional
    public void registerTeam(com.gasing.hackhub.dto.team.request.JoinHackathonRequest request) {

        TeamContext ctx = validatorService.validateTeamAndMember(
                request.getHackathonId(),
                request.getTeamId(),
                request.getUserId()
        );

        Hackathon hackathon = ctx.getHackathon();
        Team team = ctx.getTeam();

        // --- CONTROLLI ---

        // Le iscrizioni sono aperte?
        if (hackathon.getStato() != HackathonStatus.REGISTRATION_OPEN) {
            throw new RuntimeException("Le iscrizioni sono chiuse. Stato: " + hackathon.getStato());
        }

        // Scadenza passata?
        if (java.time.LocalDateTime.now().isAfter(hackathon.getScadenzaIscrizione())) {
            throw new RuntimeException("Tempo scaduto! Deadline: " + hackathon.getScadenzaIscrizione());
        }

        // Team troppo grande?
        if (team.getMembers().size() > hackathon.getDimensioneMassimaTeam()) {
            throw new RuntimeException("Il team ha troppi membri! Max: " + hackathon.getDimensioneMassimaTeam());
        }

        // Già iscritto?
        if (registrationRepository.existsByHackathonAndTeam(hackathon, team)) {
            throw new RuntimeException("Il team è già iscritto a questo evento!");
        }

        // Save
        com.gasing.hackhub.model.HackathonRegistration registration = new com.gasing.hackhub.model.HackathonRegistration();
        registration.setHackathon(hackathon);
        registration.setTeam(team);
        registration.setDataRegistrazione(java.time.LocalDate.now()); // Usa LocalDate come nel tuo model
        registration.setWinner(false);

        registrationRepository.save(registration);
    }

    // Metodo privato per il vincitore

    private void calcolaVincitore(@NotNull Hackathon hackathon) {

        HackathonRegistration winningReg = null;
        int maxScore = -1;
        boolean atLeastOneSubmissionExists = false;

        // Controllo COMPLETEZZA delle valutazioni
        for (HackathonRegistration reg : hackathon.getRegistrations()) {

            Submission submission = reg.getSubmission();

            // Se il team ha consegnato qualcosa...
            if (submission != null) {
                atLeastOneSubmissionExists = true;

                // ...DEVE per forza avere una valutazione!
                if (submission.getEvaluation() == null) {
                    throw new RuntimeException("Impossibile chiudere: Il team '" + reg.getTeam().getNome() + "' ha consegnato un progetto ma non è ancora stato valutato!");
                }

                // Se siamo qui, il voto esiste. Controlliamo se è il nuovo record.
                int score = submission.getEvaluation().getScore();
                if (score > maxScore) {
                    maxScore = score;
                    winningReg = reg;
                }
            }
        }

        // Controllo se c'è almeno un progetto in gara
        if (!atLeastOneSubmissionExists) {
            // Se nessuno ha consegnato nulla, l'hackathon si chiude senza vincitori (nulla di fatto)
            // Non lanciamo errore, semplicemente finisce così.
            return;
        }

        // Proclamazione del Vincitore
        if (winningReg != null) {
            winningReg.setWinner(true);
            registrationRepository.save(winningReg);
        } else {
            // Caso limite: Tutti hanno preso 0 o voti negativi (se possibile), o logica strana.
            // Ma col codice sopra, winningReg dovrebbe essere settato se c'è almeno una submission.
        }
    }

    // Metodo privato di utilità per non ripetere codice 3 volte
    private void assignRole(User user, Hackathon hackathon, Role role) {
        // Controllo se è già staff
        if (staffAssignmentRepository.existsByHackathonIdAndUserId(hackathon.getId(), user.getId())) {

            throw new RuntimeException("L'utente " + user.getEmail() + " è già staff in questo evento!");
        }

        StaffAssignment assignment = new StaffAssignment();
        assignment.setUser(user);
        assignment.setHackathon(hackathon);
        assignment.setRole(role);
        staffAssignmentRepository.save(assignment);

        // Se la lista è null (appena creato), la inizializziamo
        if (hackathon.getStaff() == null) {
            hackathon.setStaff(new java.util.ArrayList<>());
        }
        hackathon.getStaff().add(assignment);
    }

}
