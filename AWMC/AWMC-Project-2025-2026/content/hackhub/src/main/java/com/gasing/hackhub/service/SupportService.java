    package com.gasing.hackhub.service;
    
    import java.time.LocalDateTime;
    import java.util.List;
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    
    import com.gasing.hackhub.dto.staff.CreateSupportRequest;
    import com.gasing.hackhub.enums.RequestStatus;
    import com.gasing.hackhub.model.SupportRequest;
    import com.gasing.hackhub.model.Team;
    import com.gasing.hackhub.repository.SupportRepository;
    import com.gasing.hackhub.repository.TeamRepository;
    
    @Service
    public class SupportService {
       @Autowired
       private SupportRepository supportRepository;
    
       @Autowired
       private TeamRepository teamRepository;
    
       // Team chiede supporto: crea una nuova richiesta
       public SupportRequest createRequest(CreateSupportRequest dto) {
           // controlla esistenza team
           Team team = teamRepository.findById(dto.getTeamId())
                   .orElseThrow(() -> new RuntimeException("Team non trovato con ID: " + dto.getTeamId()));
    
           // crea l'oggetto
           SupportRequest request = new SupportRequest();
           request.setTeam(team); // Collego al team
           request.setProblema(dto.getProblema()); // Descrizione del problema
           request.setStatus(RequestStatus.OPEN); // Nasce "APERTA"
           //request.setDataCreazione(LocalDateTime.now()); // Ora attuale
    
           // Salva request in DB
           return supportRepository.save(request);
       }
    
       // Mentore pianifica una sessione di supporto: aggiorna la richiesta con il link alla call e chiude la richiesta
       public SupportRequest resolveRequest(Long requestId, String callLink) {
           SupportRequest request = supportRepository.findById(requestId)
                   .orElseThrow(() -> new RuntimeException("Richiesta non trovata"));
    
           request.setCallLink(callLink); // Segnail link alla call
           request.setStatus(RequestStatus.RESOLVED); // Segna come risolta
    
           return supportRepository.save(request);
       }
    
    
       // --- metodi di lettura
    
       public List<SupportRequest> getAllOpenRequests() {
           return supportRepository.findByStatus(RequestStatus.OPEN);
       }
    
       public List<SupportRequest> getRequestsByTeam(Long teamId) {
           return supportRepository.findByTeamId(teamId);
       }
    }
