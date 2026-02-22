package com.gasing.hackhub.dto.staff;

import com.gasing.hackhub.enums.Role;
import lombok.Data;

@Data
public class AddMentorRequest {
    private Long organizerId;   // Chi sta facendo l'operazione (l'Organizzatore)
    private Long hackathonId;   // L'ID dell'evento a cui aggiungere lo staff
    private String emailUtente; // L'email della persona da aggiungere (Il service usa findByEmail)
    private Role ruolo;         // Il ruolo da assegnare (MENTOR o JUDGE)
}