package com.axell.tukutiket.pesenan;

import com.axell.microservices.common.webmodel.WebResponse;
import com.axell.tukutiket.pesenan.acara.AcaraProxy;
import com.axell.tukutiket.pesenan.bayarbayar.BayarbayarProxy;
import com.axell.tukutiket.pesenan.uwong.UwongProxy;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class PesenanServiceImpl implements PesenanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PesenanServiceImpl.class);

    @Value("${queue.payment.verified}")
    private String paymentVerifiedEvent;
    @Value("${queue.send.email}")
    private String sendEmailEvent;
    @Value("${tukutiket.exchange}")
    private String tukutiketExchange;

    @Autowired
    private PesenanRepository pesenanRepository;
    @Autowired
    private AcaraProxy acaraProxy;
    @Autowired
    private UwongProxy uwongProxy;
    @Autowired
    private BayarbayarProxy bayarbayarProxy;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @HystrixCommand(fallbackMethod = "placeNewOrderFallback")
    @Transactional
    public OrderResponse placeNewOrder(OrderRequest orderRequest) {
        EventDetailResponse eventDetailResponse = getEventDetail(orderRequest.getEventId());

        Order newOrder = toOrder(orderRequest);
        newOrder.setTotalPrice(eventDetailResponse.getTicketPrice().multiply(BigDecimal.valueOf(orderRequest.getTicketQuantity())));
        Order savedOrder = pesenanRepository.save(newOrder);
        return toOrderResponse(savedOrder);
    }

    private OrderResponse placeNewOrderFallback(OrderRequest orderRequest, Throwable hystrixCommand) {
        LOGGER.error("placeNewOrderFallback : " + hystrixCommand.getMessage());
        return OrderResponse.builder()
                .id("id")
                .build();
    }

    private EventDetailResponse getEventDetail(String eventId) {
        WebResponse<EventDetailResponse> eventDetailResponseWebResponse = acaraProxy.getEventDetail(eventId);
        if (isResponseContainsError(eventDetailResponseWebResponse.getErrorCode()))
            throw new RuntimeException(eventDetailResponseWebResponse.getErrorCode());
        return eventDetailResponseWebResponse.getData();
    }

    private boolean isResponseContainsError(String errorCode) {
        return errorCode != null;
    }

    private Order toOrder(OrderRequest orderRequest) {
        Order order = new Order();
        BeanUtils.copyProperties(orderRequest, order);
        order.setId(UUID.randomUUID().toString());
        order.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        return order;
    }

    private OrderResponse toOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        BeanUtils.copyProperties(order, orderResponse);
        return orderResponse;
    }

    @Override
    @HystrixCommand(fallbackMethod = "getOrderDetailFallback")
    public OrderDetailResponse getOrderDetail(String orderId) {
        Order order = getOrderById(orderId);

        EventDetailResponse eventDetailResponse = getEventDetail(order.getEventId());

        PaymentDetailResponse paymentDetailResponse = getPaymentDetail(order.getBankId());

        return OrderDetailResponse.builder()
                .id(orderId)
                .eventName(eventDetailResponse.getName())
                .eventVenue(eventDetailResponse.getVenue())
                .eventDate(eventDetailResponse.getDate())
                .bankName(paymentDetailResponse.getName())
                .bankAccountNumber(paymentDetailResponse.getAccountNumber())
                .totalPrice(order.getTotalPrice())
                .ticketQuantity(order.getTicketQuantity())
                .build();
    }

    private OrderDetailResponse getOrderDetailFallback(String orderId, Throwable hystrixCommandException) {
        LOGGER.error("getOrderDetailFallback : " + hystrixCommandException.getMessage());
        return OrderDetailResponse.builder()
                .id(orderId)
                .eventName("eventName")
                .eventVenue("eventVenue")
                .eventDate(new Date())
                .bankName("bankName")
                .bankAccountNumber("bankAccountNumber")
                .ticketQuantity(1)
                .totalPrice(BigDecimal.ZERO)
                .build();
    }

    private Order getOrderById(String orderId) {
        Optional<Order> optionalOrder = pesenanRepository.findById(orderId);
        if (!optionalOrder.isPresent())
            throw new RuntimeException(ErrorCode.ORDER_NOT_FOUND.toString());
        return optionalOrder.get();
    }

    private PaymentDetailResponse getPaymentDetail(String bankId) {
        WebResponse<PaymentDetailResponse> paymentDetailResponseWebResponse = bayarbayarProxy.getPaymentDetail(bankId);
        if (isResponseContainsError(paymentDetailResponseWebResponse.getErrorCode()))
            throw new RuntimeException(paymentDetailResponseWebResponse.getErrorCode());
        return paymentDetailResponseWebResponse.getData();
    }

    @Override
    @HystrixCommand(fallbackMethod = "confirmPaymentFallback")
    @Transactional
    public OrderResponse confirmPayment(String orderId, ConfirmPaymentRequest confirmPaymentRequest) {
        Order order = getOrderById(orderId);
        confirmPaymentRequest.setTotalPrice(order.getTotalPrice());

        UserDetailResponse userDetailResponse = getUserDetail(order.getUserId());

        validatePayment(confirmPaymentRequest);

        Order verifiedPaymentOrder = verifyPayment(order);
        publishPaymentVerifiedEvent(userDetailResponse.getEmail(), orderId);

        return toOrderResponse(verifiedPaymentOrder);
    }

    private UserDetailResponse getUserDetail(String userId) {
        WebResponse<UserDetailResponse> userDetailWebResponse = uwongProxy.getUserDetail(userId);
        if (isResponseContainsError(userDetailWebResponse.getErrorCode()))
            throw new RuntimeException(userDetailWebResponse.getErrorCode());
        return userDetailWebResponse.getData();
    }

    public OrderResponse confirmPaymentFallback(String orderId, ConfirmPaymentRequest confirmPaymentRequest, Throwable hystrixCommandException) {
        LOGGER.error("confirmPaymentFallback : " + hystrixCommandException.getMessage());
        return OrderResponse.builder()
                .id(orderId)
                .build();
    }

    private void validatePayment(ConfirmPaymentRequest confirmPaymentRequest) {
        WebResponse paymentValidationResponse = bayarbayarProxy.validatePayment(confirmPaymentRequest);
        if (isResponseContainsError(paymentValidationResponse.getErrorCode()))
            throw new RuntimeException(paymentValidationResponse.getErrorCode());
    }

    private Order verifyPayment(Order order) {
        order.setOrderStatus(OrderStatus.PAYMENT_VERIFIED);
        return pesenanRepository.save(order);
    }

    private void publishPaymentVerifiedEvent(String userEmailAddress, String orderId) {
        GenerateQRCodeRequest generateQRCodeRequest = GenerateQRCodeRequest.builder()
                .userEmailAddress(userEmailAddress)
                .orderId(orderId)
                .build();
        rabbitTemplate.setExchange(tukutiketExchange);
        rabbitTemplate.convertAndSend(paymentVerifiedEvent, generateQRCodeRequest);
    }

    @RabbitListener(queues = "${queue.qrcode.created}")
    @SendTo(value = "${queue.send.email}")
    @HystrixCommand(fallbackMethod = "handleQRCodeCreatedEventFallback")
    @Transactional
    public EmailRequest handleQRCodeCreatedEvent(GenerateQRCodeResponse generateQRCodeResponse) {
        Order order = getOrderById(generateQRCodeResponse.getOrderId());
        order.setOrderStatus(OrderStatus.EMAIL_SENDING_PENDING);
        order.setQrCodeImageName(generateQRCodeResponse.getImageName());

        pesenanRepository.save(order);

        UserDetailResponse userDetailResponse = getUserDetail(order.getUserId());

        EventDetailResponse eventDetailResponse = getEventDetail(order.getEventId());

        return EmailRequest.builder()
                .orderId(order.getId())
                .username(userDetailResponse.getName())
                .emailAddress(userDetailResponse.getEmail())
                .eventName(eventDetailResponse.getName())
                .eventVenue(eventDetailResponse.getVenue())
                .eventDate(eventDetailResponse.getDate())
                .qrCodeImageName(order.getQrCodeImageName())
                .build();
    }

    private EmailRequest handleQRCodeCreatedEventFallback(GenerateQRCodeResponse generateQRCodeResponse, Throwable hystrixCommandException) {
        LOGGER.error("handleQRCodeCreatedEventFallback : " + hystrixCommandException.getMessage());
        return EmailRequest.builder()
                .orderId(generateQRCodeResponse.getOrderId())
                .username("username")
                .emailAddress("emailAddress")
                .eventName("eventName")
                .eventVenue("eventVenue")
                .eventDate(new Date())
                .qrCodeImageName(generateQRCodeResponse.getImageName())
                .build();
    }

    @RabbitListener(queues = "${queue.email.sent}")
    public void handleSentEmailEvent(EmailSentResponse emailSentResponse) {
        Order order = getOrderById(emailSentResponse.getOrderId());
        order.setOrderStatus(OrderStatus.EMAIL_SENDING_DONE);
        pesenanRepository.save(order);
    }
}
