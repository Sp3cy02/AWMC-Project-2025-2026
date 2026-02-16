package com.gasing.hackhub.repository;

import com.gasing.hackhub.model.Evaluation;
import com.gasing.hackhub.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation,Long> {

    Optional<Evaluation> findBySubmission(Submission submission);

    boolean existsBySubmission(Submission submission); // Per verificare se è già stata valutata

    // Eredita il save dall classe estesa
}
