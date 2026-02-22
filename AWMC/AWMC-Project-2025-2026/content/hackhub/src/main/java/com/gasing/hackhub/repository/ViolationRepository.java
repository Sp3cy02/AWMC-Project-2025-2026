package com.gasing.hackhub.repository;

import com.gasing.hackhub.model.ViolationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationRepository extends JpaRepository<ViolationReport, Long> {

    // Query direttamente nel metodo gestitta da spring
    List<ViolationReport> findByGestitaFalseAndReporter_Hackathon_Id(Long hackathonId);
}