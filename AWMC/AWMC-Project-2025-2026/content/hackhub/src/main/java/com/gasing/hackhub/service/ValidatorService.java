package com.gasing.hackhub.service;

import com.gasing.hackhub.dto.competizione.TeamContext;
import com.gasing.hackhub.model.Hackathon;
import com.gasing.hackhub.model.Team;
import com.gasing.hackhub.model.User;
import com.gasing.hackhub.repository.HackathonRepository;
import com.gasing.hackhub.repository.TeamRepository;
import com.gasing.hackhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidatorService {

    @Autowired private HackathonRepository hackathonRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private UserRepository userRepository;

    /**
     * Recupera le entità e verifica che l'utente sia membro del team.
     */
    public TeamContext validateTeamAndMember(Long hackathonId, Long teamId, Long userId) {

        // Recupero Entità
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon non trovato (ID: " + hackathonId + ")"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team non trovato (ID: " + teamId + ")"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato (ID: " + userId + ")"));

        // Controllo Membership
        boolean isMember = team.getMembers().stream()
                .anyMatch(m -> m.getId().equals(user.getId()));

        if (!isMember) {
            throw new RuntimeException("Errore di Validazione: L'utente " + user.getEmail() + " non fa parte del team '" + team.getNome() + "'!");
        }

        // Restituisco tutto il pacchetto pronto all'uso
        return new TeamContext(hackathon, team, user);
    }
}