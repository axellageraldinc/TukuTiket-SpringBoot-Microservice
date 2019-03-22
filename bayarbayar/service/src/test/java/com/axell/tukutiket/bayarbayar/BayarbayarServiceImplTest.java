package com.axell.tukutiket.bayarbayar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BayarbayarServiceImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private BayarbayarRepository bayarbayarRepository;

    @InjectMocks
    private BayarbayarServiceImpl bayarbayarService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findAllBankInfoSuccess() {
        when(bayarbayarRepository.findAll())
                .thenReturn(Collections.singletonList(
                        Bank.builder()
                                .id("1")
                                .name("1")
                                .accountNumber("1")
                                .build()));

        List<BankResponse> response = bayarbayarService.findAllBankInfo();

        assertThat(response, notNullValue());
        assertThat(response.isEmpty(), equalTo(false));
        assertThat(response.size(), equalTo(1));

        verify(bayarbayarRepository, times(1)).findAll();
    }

    @Test(expected = RuntimeException.class)
    public void getBankDetailFailedBankNotFound() {
        when(bayarbayarRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        bayarbayarService.getBankDetail("123");

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(ErrorCode.BANK_NOT_FOUND.toString());

        verify(bayarbayarRepository, times(1)).findById(anyString());
    }

    @Test
    public void getBankDetailSuccess() {
        when(bayarbayarRepository.findById(anyString()))
                .thenReturn(Optional.of(Bank.builder()
                        .id("1")
                        .name("1")
                        .accountNumber("1")
                        .build()));

        BankDetailResponse bankDetailResponse = bayarbayarService.getBankDetail("1");

        assertThat(bankDetailResponse, notNullValue());
        assertThat(bankDetailResponse.getName(), equalTo("1"));

        verify(bayarbayarRepository, times(1)).findById(anyString());
    }

    @Test
    public void validatePaymentSuccess() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .amountPaid(BigDecimal.valueOf(5000))
                .totalPrice(BigDecimal.valueOf(5000))
                .build();

        bayarbayarService.validatePayment(paymentRequest);
    }

    @Test(expected = RuntimeException.class)
    public void validatePaymentFailedAmountPaidLessThanTotalPrice() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .amountPaid(BigDecimal.valueOf(2000))
                .totalPrice(BigDecimal.valueOf(5000))
                .build();

        bayarbayarService.validatePayment(paymentRequest);

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(ErrorCode.AMOUNT_PAID_LESS_THAN_TOTAL_PRICE.toString());
    }

    @Test(expected = RuntimeException.class)
    public void validatePaymentFailedAmountPaidMoreThanTotalPrice() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .amountPaid(BigDecimal.valueOf(7000))
                .totalPrice(BigDecimal.valueOf(5000))
                .build();

        bayarbayarService.validatePayment(paymentRequest);

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(ErrorCode.AMOUNT_PAID_MORE_THAN_TOTAL_PRICE.toString());
    }
}