package com.gasing.hackhub.adapter;

/**
 * PATTERN ADAPTER - Componente: TARGET (Client Interface)
 * Definisce il contratto che il client (SupportService) si aspetta di usare.
 */
public interface CalendarGateway {

    String prenotaCall(String nomeMentore, String nomeTeam, String dataOra);

}
