package com.gasing.hackhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "Utente") // user non si può usare in SQL per nominare una tabella
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails { // <--- 1. AGGIUNTO implements UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id si autoincrementa nel database
    private Long id;

    @Column(nullable = false, unique=true) // unique fa si che non si possa usare l'email per due utenti
    private String email;

    @Column(nullable = false)
    private String passwordHash; // la password non deve essere mostrata in chiaro

    @Column(nullable = false) // non accetta valori vuoti
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @OneToMany(mappedBy = "user") // serve per il collegamento di un utente al suo ruolo staff che può avere in vari hackathon
    @ToString.Exclude
    @JsonIgnore
    private List<StaffAssignment> assignments;

    @ManyToOne // tanti utenti fanno parte di un team
    @JoinColumn(name = "team_id") // crea una colonna team_id nella tabella Utente
    @ToString.Exclude  // protegge debugger e log errori
    @JsonIgnore // non serve se usiamo i DTO, però lo lascio per sicurezza nei test
    private Team team;

    // =======================================================================
    // 2. METODI DI SPRING SECURITY (Non intaccano il Database o il JSON)
    // =======================================================================

    @Override
    @JsonIgnore // Ignorato dal JSON, serve solo al "Dietro le quinte" di Security
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Nessun ruolo globale per ora. I permessi specifici (es. MENTOR) 
        // li gestirai tramite la lista 'assignments'.
        return List.of(); 
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.passwordHash; // Colleghiamo il tuo campo password a Spring
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return this.email; // Colleghiamo la tua email come Username
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}