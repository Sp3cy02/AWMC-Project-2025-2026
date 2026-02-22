package com.gasing.hackhub.adapter;

import org.springframework.stereotype.Service;

/**
 * ADAPTER PATTERN
 * Questo componente adatta l'interfaccia del nostro sistema
 * a quella di un ipotetico sistema di pagamento esterno (es. PayPal/Stripe).
 */
@Service
public class PaymentAdapter implements PaymentGateway {

    @Override
    public boolean processPayment(String nomeTeam, Double importo, String ibanFittizio) {

        System.out.println(">>> [SISTEMA ESTERNO PAGAMENTI] Connessione in corso...");
        System.out.println(">>> [SISTEMA ESTERNO PAGAMENTI] Trasferimento di €" + importo + " al team " + nomeTeam);
        System.out.println(">>> [SISTEMA ESTERNO PAGAMENTI] IBAN: " + ibanFittizio);

        // Qui simuiliamo una logica: se l'importo è positivo, paga.
        if (importo > 0) {
            System.out.println(">>> [SISTEMA ESTERNO PAGAMENTI] Pagamento RIUSCITO ✅");
            return true;
        } else {
            System.out.println(">>> [SISTEMA ESTERNO PAGAMENTI] Pagamento FALLITO ❌");
            return false;
        }
    }
}