package com.axell.tukutiket.pesenan;

import com.axell.microservices.common.webmodel.WebResponse;
import com.axell.tukutiket.pesenan.acara.AcaraProxy;
import com.axell.tukutiket.pesenan.bayarbayar.BayarbayarProxy;
import com.axell.tukutiket.pesenan.uwong.UwongProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.NONE)
@DirtiesContext
public class PesenanServiceImplTest {

    @MockBean
    private AcaraProxy acaraProxy;
    @MockBean
    private UwongProxy uwongProxy;
    @MockBean
    private BayarbayarProxy bayarbayarProxy;

    @Mock
    private PesenanRepository pesenanRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PesenanServiceImpl pesenanService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void placeNewOrderSuccess() {
        when(acaraProxy.getEventDetail(anyString()))
                .thenReturn(WebResponse.OK(
                        EventDetailResponse.builder()
                                .id("1")
                                .name("1")
                                .date(new Date())
                                .venue("1")
                                .status("TO_BE_HELD")
                                .ticketPrice(BigDecimal.valueOf(1000))
                                .build()));
        when(pesenanRepository.save(any(Order.class)))
                .thenReturn(Order.builder()
                        .id("1")
                        .bankId("1")
                        .eventId("1")
                        .userId("1")
                        .orderStatus(OrderStatus.WAITING_PAYMENT)
                        .qrCodeImageName("1.png")
                        .ticketQuantity(1)
                        .totalPrice(BigDecimal.valueOf(1000))
                        .build());

        OrderResponse orderResponse = pesenanService.placeNewOrder(OrderRequest.builder()
                .userId("1")
                .bankId("1")
                .eventId("1")
                .ticketQuantity(1)
                .build());

        assertThat(orderResponse, notNullValue());
        assertThat(orderResponse.getId(), equalTo("1"));

        InOrder inOrder = Mockito.inOrder(acaraProxy, pesenanRepository);
        inOrder.verify(acaraProxy, times(1)).getEventDetail(anyString());
        inOrder.verify(pesenanRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void placeNewOrderFallback() {

    }

    @Test
    public void getOrderDetailSuccess() {
        when(pesenanRepository.findById(anyString()))
                .thenReturn(Optional.of(Order.builder()
                        .id("1")
                        .bankId("1")
                        .eventId("1")
                        .userId("1")
                        .orderStatus(OrderStatus.WAITING_PAYMENT)
                        .qrCodeImageName("1.png")
                        .ticketQuantity(1)
                        .totalPrice(BigDecimal.valueOf(1000))
                        .build()));
        when(acaraProxy.getEventDetail(anyString()))
                .thenReturn(WebResponse.OK(
                        EventDetailResponse.builder()
                                .id("1")
                                .name("1")
                                .date(new Date())
                                .venue("1")
                                .status("TO_BE_HELD")
                                .ticketPrice(BigDecimal.valueOf(1000))
                                .build()));
        when(bayarbayarProxy.getPaymentDetail(anyString()))
                .thenReturn(WebResponse.OK(
                        PaymentDetailResponse.builder()
                                .name("1")
                                .accountNumber("1")
                                .build()));

        OrderDetailResponse orderDetailResponse = pesenanService.getOrderDetail("1");

        assertThat(orderDetailResponse, notNullValue());
        assertThat(orderDetailResponse.getId(), equalTo("1"));

        InOrder inOrder = Mockito.inOrder(pesenanRepository, acaraProxy, bayarbayarProxy);
        inOrder.verify(pesenanRepository, times(1)).findById(anyString());
        inOrder.verify(acaraProxy, times(1)).getEventDetail(anyString());
        inOrder.verify(bayarbayarProxy, times(1)).getPaymentDetail(anyString());
    }

    @Test
    public void getOrderDetailFallback() {

    }

    @Test
    public void confirmPaymentSuccess() {
        when(pesenanRepository.findById(anyString()))
                .thenReturn(Optional.of(Order.builder()
                        .id("1")
                        .bankId("1")
                        .eventId("1")
                        .userId("1")
                        .orderStatus(OrderStatus.WAITING_PAYMENT)
                        .qrCodeImageName("1.png")
                        .ticketQuantity(1)
                        .totalPrice(BigDecimal.valueOf(1000))
                        .build()));
        when(uwongProxy.getUserDetail(anyString()))
                .thenReturn(WebResponse.OK(
                        UserDetailResponse.builder()
                                .name("1")
                                .email("1")
                                .build()));
        when(bayarbayarProxy.validatePayment(any(ConfirmPaymentRequest.class)))
                .thenReturn(WebResponse.OK());
        when(pesenanRepository.save(any(Order.class)))
                .thenReturn(Order.builder()
                        .id("1")
                        .bankId("1")
                        .eventId("1")
                        .userId("1")
                        .orderStatus(OrderStatus.PAYMENT_VERIFIED)
                        .qrCodeImageName("1.png")
                        .ticketQuantity(1)
                        .totalPrice(BigDecimal.valueOf(1000))
                        .build());
        doNothing().when(rabbitTemplate).setExchange(anyString());
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), any(GenerateQRCodeRequest.class));

        OrderResponse orderResponse = pesenanService.confirmPayment(anyString(), ConfirmPaymentRequest.builder()
                .amountPaid(BigDecimal.valueOf(1000))
                .totalPrice(BigDecimal.valueOf(1000))
                .build());

        assertThat(orderResponse, notNullValue());
        assertThat(orderResponse.getId(), equalTo("1"));

        InOrder inOrder = inOrder(pesenanRepository, uwongProxy, bayarbayarProxy, rabbitTemplate);
        inOrder.verify(pesenanRepository, times(1)).findById(anyString());
        inOrder.verify(uwongProxy, times(1)).getUserDetail(anyString());
        inOrder.verify(bayarbayarProxy, times(1)).validatePayment(any(ConfirmPaymentRequest.class));
        inOrder.verify(pesenanRepository, times(1)).save(any(Order.class));
        inOrder.verify(rabbitTemplate, times(1)).setExchange(anyString());
        inOrder.verify(rabbitTemplate, times(1)).convertAndSend(anyString(), any(GenerateQRCodeRequest.class));
    }

    @Test
    public void confirmPaymentFallback() {
    }

    @Test
    public void handleQRCodeCreatedEventSuccess() {
    }

    @Test
    public void handleQRCodeCreatedEventFallback() {

    }

    @Test
    public void handleSentEmailEvent() {
    }
}