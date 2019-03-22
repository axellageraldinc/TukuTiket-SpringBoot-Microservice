package com.axell.tukutiket.bayarbayar;

import java.util.List;

public interface BayarbayarService {
    List<BankResponse> findAllBankInfo();

    BankDetailResponse getBankDetail(String bankId);

    void validatePayment(PaymentRequest paymentRequest);
}
