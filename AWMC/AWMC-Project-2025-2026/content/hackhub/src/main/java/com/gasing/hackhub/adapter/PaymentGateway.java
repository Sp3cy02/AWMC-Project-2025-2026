package com.gasing.hackhub.adapter;

public interface PaymentGateway {

    boolean processPayment(String nomeTeam, Double importo, String ibanFittizio);

}
