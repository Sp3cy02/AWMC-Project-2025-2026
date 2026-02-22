package com.gasing.hackhub.service;

import com.gasing.hackhub.adapter.PaymentGateway;
import com.gasing.hackhub.dto.competizione.TeamContext;
import com.gasing.hackhub.dto.hackathon.HackathonDTO;
import com.gasing.hackhub.dto.hackathon.ProclaimWinnerRequest;
import com.gasing.hackhub.dto.team.request.JoinHackathonRequest;
import com.gasing.hackhub.enums.HackathonStatus;
import com.gasing.hackhub.enums.Role;
import com.gasing.hackhub.model.*;
import com.gasing.hackhub.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Autowired
    private PaymentGateway paymentAdapter;

    @Autowired
    private TeamRepository teamRepository;


    @Transactional
    public Hackathon createHackathon(HackathonDTO request) {

        // Controllo se esiste gia
        if (hackathonRepository.existsByNome(request.getNome())) {
            throw new RuntimeException("Esiste già un hackathon con questo nome!");
        }

        // Controllo che ci sia almeno un mentore (come da specifiche "uno o più")
        if (request.getMentorIds() == null || request.getMentorIds().isEmpty()) {
            throw new RuntimeException("Devi assegnare almeno un Mentore!");
        }

        // Controllo lo staff

        // L'Organizzatore
        User organizerUser = userRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("Organizzatore non trovato"));

        // Il Giudice
        User judgeUser = userRepository.findById(request.getJudgeId())
                .orElseThrow(() -> new RuntimeException("Giudice non trovato"));

        // Salvo
        Hackathon hackathon = new Hackathon();
        hackathon.setNome(request.getNome());
        hackathon.setRegolamento(request.getRegolamento());
        hackathon.setLuogo(request.getLuogo());
        hackathon.setPremio(request.getPremio());
        hackathon.setDimensioneMassimaTeam(request.getDimensioneMassimaTeam());

        hackathon.setDataInizio(request.getDataInizio());
        hackathon.setDataFine(request.getDataFine());
        hackathon.setScadenzaIscrizione(request.getScadenzaIscrizione());

        hackathon.setStato(HackathonStatus.REGISTRATION);

        // Salvo per ottenere l'ID
        hackathon = hackathonRepository.save(hackathon);

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

    @Transactional
    public void advancePhase(Long hackathonId, Long organizerId) {

        // Recupero Hackathon
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon non trovato"));

        // Controllo Sicurezza
        validatorService.requireStaffRole(hackathonId, organizerId, Role.ORGANIZER);


        switch (hackathon.getStato()) {
            case REGISTRATION:
                hackathon.setStato(HackathonStatus.ONGOING);
                break;

            case ONGOING:
                hackathon.setStato(HackathonStatus.EVALUATION);
                break;

            case EVALUATION:
                // Se è tutto ok si chiude altrimenti lancia eccezione
                hackathon.setStato(HackathonStatus.CONCLUDED);
                break;

                // Viene concluso quando viene scelto il vincitore
            default:
                throw new RuntimeException("Stato non valido.");
        }

        // Salvo
        hackathonRepository.save(hackathon);
    }

    // ISCRIZIONE DEL TEAM ALL?HACKATHON

    @Transactional
    public void registerTeam(JoinHackathonRequest request) {

        TeamContext ctx = validatorService.validateTeamAndMember(
                request.getHackathonId(),
                request.getTeamId(),
                request.getUserId()
        );

        Hackathon hackathon = ctx.getHackathon();
        Team team = ctx.getTeam();

        // Controlli

        // Le iscrizioni sono aperte?
        if (hackathon.getStato() != HackathonStatus.REGISTRATION) {
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

    // Lista di tutti gli Hackathon (Per Utenti e Visitatori)
    public List<Hackathon> getAllHackathons() {
        return hackathonRepository.findAll();
    }

    // Dettaglio singolo Hackathon
    public Hackathon getHackathonById(Long id) {
        return hackathonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hackathon con ID " + id + " non trovato!"));
    }

    // Metodo privato per il vincitore

    @Transactional
    public void proclaimWinner(ProclaimWinnerRequest request) {

        // Recupero Hackathon
        Hackathon hackathon = hackathonRepository.findById(request.getHackathonId())
                .orElseThrow(() -> new RuntimeException("Hackathon non trovato"));

        // Controllo Stato
        if (hackathon.getStato() != HackathonStatus.EVALUATION) {
            throw new RuntimeException("Impossibile proclamare vincitore: l'hackathon non è in fase di valutazione.");
        }

        // Controllo Permessi
        validatorService.requireStaffRole(request.getHackathonId(), request.getOrganizerId(), Role.ORGANIZER);

        // Controllo Valutazioni (Opzionale, come dicevamo prima)
        boolean ciSonoSubmissionNonValutate = hackathon.getRegistrations().stream()
                .filter(reg -> reg.getSubmission() != null)
                .anyMatch(reg -> reg.getSubmission().getEvaluation() == null);

        if (ciSonoSubmissionNonValutate) {
            throw new RuntimeException("Attenzione: Non puoi chiudere l'evento finché il Giudice non ha valutato tutti i progetti consegnati!");
        }

        // RECUPERO IL TEAM (Ecco la modifica!)
        Team winnerTeam = teamRepository.findById(request.getWinningTeamId())
                .orElseThrow(() -> new RuntimeException("Team con ID " + request.getWinningTeamId() + " non trovato."));

        // Recupero la registrazione
        HackathonRegistration winnerRegistration = registrationRepository
                .findByHackathonAndTeam(hackathon, winnerTeam)
                .orElseThrow(() -> new RuntimeException("Il team selezionato non è iscritto a questo hackathon."));

        // Setto il vincitore e chiudo
        winnerRegistration.setWinner(true);
        registrationRepository.save(winnerRegistration);

        hackathon.setStato(HackathonStatus.CONCLUDED);
        hackathonRepository.save(hackathon);
    }

    @Transactional
    public String erogaPremio(Long hackathonId, Long organizerId) {

        // Recupero Hackathon
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon non trovato"));

        // Controllo Sicurezza (Solo Organizzatore)
        validatorService.requireStaffRole(hackathonId, organizerId, Role.ORGANIZER);

        // Controllo Stato: Si paga solo ad evento concluso
        if (hackathon.getStato() != HackathonStatus.CONCLUDED) {
            throw new RuntimeException("L'evento non è ancora concluso! Devi prima proclamare il vincitore.");
        }

        // Recupero il Vincitore
        HackathonRegistration winner = hackathon.getRegistrations().stream()
                .filter(HackathonRegistration::isWinner)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nessun vincitore è stato proclamato per questo evento."));

        // Uso l'Adapter
        String nomeTeam = winner.getTeam().getNome();
        Double premio = hackathon.getPremio();
        String ibanFake = "IT00HACKHUB" + winner.getTeam().getId(); // Genero un iban finto da passare come esempio

        boolean esito = paymentAdapter.processPayment(nomeTeam, premio, ibanFake);

        if (esito) {
            return "Pagamento di €" + premio + " al team '" + nomeTeam + "' avviato con successo!";
        } else {
            throw new RuntimeException("Errore del sistema esterno: Pagamento rifiutato.");
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

    public List<Team> getRegisteredTeams(Long hackathonId) {

    // opzionale: controllo esistenza hackathon (messaggio migliore)
    hackathonRepository.findById(hackathonId)
            .orElseThrow(() -> new RuntimeException("Hackathon con ID " + hackathonId + " non trovato!"));

    return registrationRepository.findByHackathon_Id(hackathonId)
            .stream()
            .map(HackathonRegistration::getTeam)
            .distinct()
            .toList();
    }

    
}
