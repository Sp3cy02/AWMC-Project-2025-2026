package com.gasing.hackhub.service;

import com.gasing.hackhub.dto.team.request.CreateTeamRequest;
import com.gasing.hackhub.dto.team.request.InviteMemberRequest;
import com.gasing.hackhub.dto.team.response.InviteMemberResponse;
import com.gasing.hackhub.dto.team.response.PendingInviteDTO;
import com.gasing.hackhub.dto.team.response.TeamMemberDTO;
import com.gasing.hackhub.enums.InviteStatus;
import com.gasing.hackhub.model.Team;
import com.gasing.hackhub.model.TeamInvitation;
import com.gasing.hackhub.model.User;
import com.gasing.hackhub.repository.TeamInvitationRepository;
import com.gasing.hackhub.repository.TeamRepository;
import com.gasing.hackhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamInvitationRepository teamInvitationRepository;

    // -----------------------------
    // CREA TEAM
    // -----------------------------
    @Transactional
    public Team createTeam(CreateTeamRequest request) {
        User creator = userRepository.findById(request.getCreatorUserId())
                .orElseThrow(() -> new RuntimeException("Utente creatore non trovato"));

        if (creator.getTeam() != null) {
            throw new RuntimeException("L'utente fa già parte di un team!");
        }

        // attenzione: nel tuo progetto prima usavi existsByNome(...)
        // qui assumo che TeamRepository abbia existsByNome(String nome)
        if (teamRepository.existsByNome(request.getNomeTeam())) {
            throw new RuntimeException("Esiste già un team con questo nome!");
        }

        Team newTeam = new Team();
        newTeam.setNome(request.getNomeTeam());

        newTeam = teamRepository.save(newTeam);

        // aggiungo il creatore ai membri
        newTeam.getMembers().add(creator);
        creator.setTeam(newTeam);
        userRepository.save(creator);

        return newTeam;
    }

    // -----------------------------
    // INVITA MEMBRO (via email)
    // POST /api/teams/invite
    // -----------------------------
    @Transactional
    public void inviteMember(InviteMemberRequest request) {
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team non trovato"));

        User destinatario = userRepository.findByEmail(request.getEmailUtente())
                .orElseThrow(() -> new RuntimeException("Nessun utente trovato con questa email"));

        if (destinatario.getTeam() != null) {
            throw new RuntimeException("L'utente fa già parte di un altro team!");
        }

        // evita invito doppio PENDING
        boolean invitoGiaEsistente = team.getInviti().stream()
                .anyMatch(invito ->
                        invito.getReceiver().getId().equals(destinatario.getId())
                                && invito.getStatus() == InviteStatus.PENDING
                );

        if (invitoGiaEsistente) {
            throw new RuntimeException("Hai già invitato questo utente ed è in attesa!");
        }

        TeamInvitation invitation = new TeamInvitation();
        invitation.setTeam(team);
        invitation.setReceiver(destinatario);
        invitation.setStatus(InviteStatus.PENDING);

        // aggiungo all’elenco inviti del team
        team.getInviti().add(invitation);

        // salvo (così scatta insert su team_invitation)
        teamRepository.save(team);
    }

    // -----------------------------
    // RISPONDI INVITO (accetta/rifiuta)
    // POST /api/teams/respond-invite
    // -----------------------------
    @Transactional
    public void rispondiInvito(InviteMemberResponse request) {
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team non trovato"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        TeamInvitation invito = team.getInviti().stream()
                .filter(i -> i.getReceiver().getId().equals(user.getId())
                        && i.getStatus() == InviteStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nessun invito in attesa trovato per te!"));

        if (request.isAccetta()) {
            if (user.getTeam() != null) {
                throw new RuntimeException("Fai già parte di un team! Devi uscire prima di accettare un nuovo invito.");
            }

            invito.setStatus(InviteStatus.ACCEPTED);
            user.setTeam(team);
            userRepository.save(user);
        } else {
            invito.setStatus(InviteStatus.REJECTED);
        }

        teamRepository.save(team);
    }

    // -----------------------------
    // LISTA MEMBRI TEAM (DTO pulito)
    // GET /api/teams/{teamId}/members
    // -----------------------------
    @Transactional(readOnly = true)
    public List<TeamMemberDTO> getMembersDTO(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team non trovato"));

        return team.getMembers().stream()
                .map(u -> new TeamMemberDTO(u.getId(), u.getNome(), u.getCognome(), u.getEmail()))
                .toList();
    }

    // -----------------------------
    // LISTA INVITI PENDING DI UN UTENTE (DTO pulito)
    // GET /api/teams/invitations/by-user?userId=...
    // -----------------------------
    @Transactional(readOnly = true)
    public List<PendingInviteDTO> getPendingInvitesForUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        List<TeamInvitation> invites =
                teamInvitationRepository.findByReceiver_IdAndStatus(userId, InviteStatus.PENDING);

        return invites.stream()
                .map(i -> new PendingInviteDTO(
                        i.getId(),
                        i.getTeam().getId(),
                        i.getTeam().getNome(),
                        i.getStatus()
                ))
                .toList();
    }
}