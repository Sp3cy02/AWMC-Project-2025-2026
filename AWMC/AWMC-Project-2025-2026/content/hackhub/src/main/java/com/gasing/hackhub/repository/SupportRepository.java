package com.gasing.hackhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gasing.hackhub.enums.RequestStatus;
import com.gasing.hackhub.model.Hackathon;
import com.gasing.hackhub.model.StaffAssignment;
import com.gasing.hackhub.model.SupportRequest;

@Repository
public interface SupportRepository extends JpaRepository<SupportRequest, Long> {
   // Trova tutte le richieste di supporto per un determinato hackathon
   public List<SupportRequest> findByHackathon(Hackathon hackathon);

   // Trova tutte le richieste di supporto assegnate a un determinato mentore
   public List<SupportRequest> findByMentor(StaffAssignment mentor);

   // Trova tutte le richieste di supporto con un determinato stato
   public List<SupportRequest> findByStatus(RequestStatus status);

   // Trova tutte le richieste di supporto per un determinato team
   public List<SupportRequest> findByTeamId(Long teamId);
}
