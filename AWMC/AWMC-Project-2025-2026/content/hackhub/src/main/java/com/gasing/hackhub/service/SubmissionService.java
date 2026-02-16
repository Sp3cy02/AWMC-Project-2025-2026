package com.gasing.hackhub.service;

import com.gasing.hackhub.dto.competizione.SubmitProjectRequest;
import com.gasing.hackhub.dto.competizione.TeamContext;
import com.gasing.hackhub.enums.HackathonStatus;
import com.gasing.hackhub.model.*;
import com.gasing.hackhub.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ValidatorService validatorService;

    @Transactional
    public Submission submitProject(SubmitProjectRequest request) {

        // Recupero le Entità base (Hackathon, Team, User)
        TeamContext ctx = validatorService.validateTeamAndMember(
                request.getHackathonId(),
                request.getTeamId(),
                request.getUserId()
        );

        // Estrai gli oggetti dal contesto (senza rifare query!)
        Hackathon hackathon = ctx.getHackathon();
        Team team = ctx.getTeam();

        if (hackathon.getStato() != HackathonStatus.ONGOING) {
            throw new RuntimeException("Non puoi inviare progetti ora! L'Hackathon è in fase: " + hackathon.getStato());
        }

        // RECUPERO LA REGISTRAZIONE
        // Qui usiamo il metodo che ti ho fatto aggiungere poco fa
        HackathonRegistration registration = registrationRepository.findByHackathonAndTeam(hackathon, team)
                .orElseThrow(() -> new RuntimeException("Errore: Il team non risulta iscritto a questo hackathon!"));


        // CONTROLLO SCADENZA
        // Se adesso (now) è dopo la fine dell'evento -> Errore
        if (LocalDateTime.now().isAfter(hackathon.getDataFine())) {
            throw new RuntimeException("Tempo scaduto! L'hackathon è concluso, non puoi più inviare progetti.");
        }


        // Cerco se esiste già una submission per questa registrazione
        Submission submission = submissionRepository.findByRegistration(registration)
                .orElse(null); // Se non c'è, torna null

        if (submission == null) {
            // PRIMA VOLTA -> Creo un oggetto nuovo
            submission = new Submission();
            submission.setRegistration(registration);
        } else {
            // Se esiste già, controlliamo se il giudice ha già messo il voto.
            if (submission.getEvaluation() != null) {
                throw new RuntimeException("Impossibile aggiornare: il progetto è già stato valutato dal giudice!");
            }
            // Se esisteva già (submission != null), non faccio 'new' ma aggiorno i campi qui sotto.
            // Questo permette al team di correggere il link o la descrizione finché c'è tempo.
        }
            // Imposto i dati
            submission.setRepositoryLink(request.getRepositoryLink());
            submission.setDescrizione(request.getDescrizione());

            // Aggiorno la data di invio all'istante attuale
            submission.setDataInvio(LocalDateTime.now());

            // Salvo nel DB
            return submissionRepository.save(submission);
        }
}
