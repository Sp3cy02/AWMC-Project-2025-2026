package com.gasing.hackhub.repository;

import com.gasing.hackhub.model.HackathonRegistration;
import com.gasing.hackhub.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByRegistration(HackathonRegistration registration);

    boolean existsByRegistration(HackathonRegistration registration);

    // Eredita dalla JpaRepo il metodo save

}
