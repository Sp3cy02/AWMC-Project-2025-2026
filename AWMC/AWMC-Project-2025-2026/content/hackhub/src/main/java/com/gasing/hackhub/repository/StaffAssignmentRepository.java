package com.gasing.hackhub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gasing.hackhub.enums.Role;
import com.gasing.hackhub.model.StaffAssignment;

@Repository
public interface StaffAssignmentRepository extends JpaRepository<StaffAssignment, Long> {
    // Restituisce tutto lo staff di un determinato Hackathon
    List<StaffAssignment> findByHackathonId(Long hackathonId);

    // Restituisce tutti i ruoli di un utente in vari hackathon
    List<StaffAssignment> findByUserId(Long userId);

    // Controlla se un utente è già assegnato come staff in un hackathon specifico
    boolean existsByHackathonIdAndUserId(Long hackathonId, Long userId);

    Optional<StaffAssignment> findByHackathonIdAndUserId(Long hackathonId, Long userId);

    boolean existsByHackathonIdAndUserIdAndRole(Long hackathonId, Long userId, Role role);

}
