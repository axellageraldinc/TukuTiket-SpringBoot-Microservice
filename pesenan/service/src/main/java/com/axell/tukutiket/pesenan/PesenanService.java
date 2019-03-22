package com.axell.tukutiket.pesenan;

public interface PesenanService {
    OrderResponse placeNewOrder(OrderRequest orderRequest);

    OrderDetailResponse getOrderDetail(String orderId);

    OrderResponse confirmPayment(String orderId, ConfirmPaymentRequest confirmPaymentRequest);
}
