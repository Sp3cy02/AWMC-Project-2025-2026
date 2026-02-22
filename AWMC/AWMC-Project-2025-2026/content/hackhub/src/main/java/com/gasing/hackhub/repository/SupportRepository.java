package com.gasing.hackhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gasing.hackhub.enums.RequestStatus;
import com.gasing.hackhub.model.SupportRequest;

@Repository
public interface SupportRepository extends JpaRepository<SupportRequest, Long> {

    // Trova le richieste di un team specifico
    List<SupportRequest> findByTeamId(Long teamId);

    // Entra dentro l'oggetto mentor, poi dentro user e cerca l'id.
    List<SupportRequest> findByMentor_User_Id(Long userId);
}