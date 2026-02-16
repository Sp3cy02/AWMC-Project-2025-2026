package com.gasing.hackhub.service;

import com.gasing.hackhub.dto.team.request.CreateTeamRequest;
import com.gasing.hackhub.dto.team.request.InviteMemberRequest;
import com.gasing.hackhub.dto.team.response.InviteMemberResponse;
import com.gasing.hackhub.enums.InviteStatus;
import com.gasing.hackhub.model.Team;
import com.gasing.hackhub.model.TeamInvitation;
import com.gasing.hackhub.model.User;
import com.gasing.hackhub.repository.TeamRepository;
import com.gasing.hackhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// service dice a spring che qui dentro c'è la logica del programma
// in pratica è il cervello che fa i calcoli prima di salvare nel db
@Service
public class TeamService {

    // autowired serve a farci dare da spring i repository già pronti
    // senza dover fare new repository ogni volta ci pensa lui
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;


    // transactional serve se qualcosa va storto a metà del metodo
    // lui annulla tutto in automatico così non restano dati a metà nel db
    @Transactional
    public Team createTeam(CreateTeamRequest request) {
        // prima controllo se esiste quello che vuole creare il team
        User creator = userRepository.findById(request.getCreatorUserId())
                .orElseThrow(() -> new RuntimeException("Utente creatore non trovato"));

        // poi vedo se sta già in un altro team perchè non può starne in due
        if (creator.getTeam() != null) {
            throw new RuntimeException("L'utente fa già parte di un team!");
        }

        // controllo pure se il nome del team è già preso
        if (teamRepository.existsByNome(request.getNomeTeam())) {
            throw new RuntimeException("Esiste già un team con questo nome!");
        }

        // se è tutto ok creo il team nuovo
        Team newTeam = new Team();
        newTeam.setNome(request.getNomeTeam());

        // salvo il team così il db gli da un id
        newTeam = teamRepository.save(newTeam);

        // Aggiungo manualmente l'utente alla lista del team in memoria
        // così quando restituisco l'oggetto 'newTeam' al frontend, la lista non è vuota.
        newTeam.getMembers().add(creator);

        // e infine dico all'utente che questo è il suo nuovo team e salvo l'utente
        creator.setTeam(newTeam);
        userRepository.save(creator);


        return newTeam;
    }

    @Transactional
    public void inviteMember(InviteMemberRequest request) {
        // recupero il team che sta mandando l'invito
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team non trovato"));

        // cerco l'amico da invitare usando la mail
        User destinatario = userRepository.findByEmail(request.getEmailUtente())
                .orElseThrow(() -> new RuntimeException("Nessun utente trovato con questa email"));

        // se l'amico ha già un team non possiamo invitarlo quindi blocco tutto
        if (destinatario.getTeam() != null) {
            throw new RuntimeException("L'utente fa già parte di un altro team!");
        }

        // qui controllo se per caso non l'abbiamo già invitato ed è ancora in attesa
        // uso stream che è un modo veloce per filtrare la lista degli inviti senza fare cicli for
        boolean invitoGiaEsistente = team.getInviti().stream()
                .anyMatch(invito -> invito.getReceiver().getId().equals(destinatario.getId())
                        && invito.getStatus() == InviteStatus.PENDING);

        if (invitoGiaEsistente) {
            throw new RuntimeException("Hai già invitato questo utente ed è in attesa!");
        }

        // creo l'oggetto invito e ci metto dentro chi invia e chi riceve
        TeamInvitation invitation = new TeamInvitation();
        invitation.setTeam(team);
        invitation.setReceiver(destinatario);
        invitation.setStatus(InviteStatus.PENDING); // lo stato iniziale è in attesa

        // aggiungo l'invito alla lista del team e salvo il team
        // grazie a una cosa che si chiama cascade spring salva da solo anche l'invito
        team.getInviti().add(invitation);

        teamRepository.save(team);
    }

    // --- 3. RISPONDERE ALL'INVITO ---

    @Transactional
    public void rispondiInvito(InviteMemberResponse request) {

        // recupero team e utente dal database
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team non trovato"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // qui devo trovare l'invito giusto dentro la lista del team
        // cerco quello indirizzato a me che è ancora in stato pending
        TeamInvitation invito = team.getInviti().stream()
                .filter(i -> i.getReceiver().getId().equals(user.getId())
                        && i.getStatus() == InviteStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nessun invito in attesa trovato per te!"));

        if (request.isAccetta()) {
            // se ha detto si cambio lo stato in accepted
            invito.setStatus(InviteStatus.ACCEPTED);

            // e aggiorno l'utente mettendogli il team
            user.setTeam(team);
            userRepository.save(user);
        } else {
            // se ha detto no metto rejected e basta
            invito.setStatus(InviteStatus.REJECTED);
        }

        // alla fine salvo il team così si aggiorna lo stato dell'invito
        teamRepository.save(team);
    }
}
