package com.gasing.hackhub.adapter;

import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * PATTERN ADAPTER - Componente: ADAPTER
 * Implementa l'interfaccia target e adatta la chiamata al sistema esterno.
 */
@Service
public class CalendarAdapter implements CalendarGateway{

    @Override
    public String prenotaCall(String nomeMentore, String nomeTeam, String dataOra) {

        // Qui simuliamo la logica dell'Adaptee (il sistema esterno Google/Zoom)
        System.out.println(">>> [CALENDAR ADAPTER] Richiesta inoltrata al sistema esterno...");
        System.out.println(">>> [CALENDAR ADAPTER] Host: " + nomeMentore + ", Guest: " + nomeTeam);

        // Simulazione risposta esterna
        String externalLink = "https://meet.google.com/" + UUID.randomUUID().toString().substring(0, 8);

        return externalLink;
    }
}