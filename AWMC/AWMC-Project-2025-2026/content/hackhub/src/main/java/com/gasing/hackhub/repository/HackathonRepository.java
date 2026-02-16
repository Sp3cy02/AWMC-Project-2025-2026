package com.gasing.hackhub.repository;

import com.gasing.hackhub.model.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {

   // Eredita i metodi findAll, findById e save dalla classe estesa

    // Serve per controllare se esiste già un evento con lo stesso nome
    // (Così evitiamo di creare due hackathon uguali)
    boolean existsByNome(String nome);

    Optional<Hackathon> findByNome(String nome);
}
