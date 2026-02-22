package com.gasing.hackhub.repository;

import com.gasing.hackhub.enums.InviteStatus;
import com.gasing.hackhub.model.TeamInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {

    //inviti PENDING per utente (receiver)
    List<TeamInvitation> findByReceiver_IdAndStatus(Long receiverId, InviteStatus status);
}