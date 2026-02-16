package com.gasing.hackhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gasing.hackhub.model.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    // i metodi di base per la gestione dei team (save, findbyid, exists) sono già forniti da JpaRepository

    public boolean existsByNome(String nome);

}
