package com.gasing.hackhub.repository;

import com.gasing.hackhub.model.Hackathon;
import com.gasing.hackhub.model.HackathonRegistration;
import com.gasing.hackhub.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<HackathonRegistration, Long> {

    // Controlla se esiste già una registrazione per questa coppia Team-Hackathon
    boolean existsByHackathonAndTeam(Hackathon hackathon, Team team);

    List<HackathonRegistration> findByHackathon(Hackathon hackathon);

    // Ci serve per recuperare la registrazione specifica
    Optional<HackathonRegistration> findByHackathonAndTeam(Hackathon hackathon, Team team);
}
