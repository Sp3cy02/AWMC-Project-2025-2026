package com.gasing.hackhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gasing.hackhub.enums.RequestStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportRequest {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @Column(nullable = false, length = 1000)
   private String problema;  // descrizione del problema che il team sta affrontando

   @Column // il link viene inserito dopo quindi può essere null
   private String callLink;  // link alla videochiamata per il supporto

   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
   private RequestStatus status = RequestStatus.OPEN; // Impostiamo un default, es. PENDING o OPEN

   @ManyToOne(optional = false) // Una richiesta deve per forza avere un team
   @JoinColumn(name = "team_id", nullable = false) // crea una colonna team_id nella tabella SupportRequest
   @ToString.Exclude  // protegge debugger e log errori
   @JsonIgnore // non serve se usiamo i DTO, però lo lascio per sicurezza nei test
   private Team team;  // il team che ha fatto la richiesta

   @ManyToOne // le richieste si riferiscono ad un mentore che è opzionale all'inizio
   @JoinColumn(name = "mentor_id", nullable = true) // crea una colonna staffAssignment_id nella tabella Utente
   @ToString.Exclude  // protegge debugger e log errori
   @JsonIgnore // non serve se usiamo i DTO, però lo lascio per sicurezza nei test
   private StaffAssignment mentor;  // il mentore assegnato alla richiesta

}
