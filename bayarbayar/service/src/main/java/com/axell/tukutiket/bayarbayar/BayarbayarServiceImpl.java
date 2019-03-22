package com.axell.tukutiket.bayarbayar;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BayarbayarServiceImpl implements BayarbayarService {

    @Autowired
    private BayarbayarRepository bayarbayarRepository;

    @Override
    public List<BankResponse> findAllBankInfo() {
        List<Bank> bankList = bayarbayarRepository.findAll();
        return toPaymentResponseList(bankList);
    }

    private List<BankResponse> toPaymentResponseList(List<Bank> bankList) {
        return bankList
                .stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toList());
    }

    private BankResponse toPaymentResponse(Bank bank) {
        BankResponse bankResponse = new BankResponse();
        BeanUtils.copyProperties(bank, bankResponse);
        return bankResponse;
    }

    @Override
    public BankDetailResponse getBankDetail(String bankId) {
        Optional<Bank> optionalBank = bayarbayarRepository.findById(bankId);
        if (!optionalBank.isPresent())
            throw new RuntimeException(ErrorCode.BANK_NOT_FOUND.toString());

        Bank bank = optionalBank.get();

        return toBankDetailResponse(bank);
    }

    private BankDetailResponse toBankDetailResponse(Bank bank) {
        BankDetailResponse bankDetailResponse = new BankDetailResponse();
        BeanUtils.copyProperties(bank, bankDetailResponse);
        return bankDetailResponse;
    }

    @Override
    public void validatePayment(PaymentRequest paymentRequest) {
        if (isAmountPaidLessThanTotalPrice(paymentRequest.getAmountPaid(), paymentRequest.getTotalPrice())) {
            throw new RuntimeException(ErrorCode.AMOUNT_PAID_LESS_THAN_TOTAL_PRICE.toString());
        }
        if (isAmountPaidMoreThanTotalPrice(paymentRequest.getAmountPaid(), paymentRequest.getTotalPrice())) {
            throw new RuntimeException(ErrorCode.AMOUNT_PAID_MORE_THAN_TOTAL_PRICE.toString());
        }
    }

    private boolean isAmountPaidLessThanTotalPrice(BigDecimal amountPaid, BigDecimal totalPrice) {
        return amountPaid.compareTo(totalPrice) < 0;
    }

    private boolean isAmountPaidMoreThanTotalPrice(BigDecimal amountPaid, BigDecimal totalPrice) {
        return amountPaid.compareTo(totalPrice) > 0;
    }
}
