package com.axell.tukutiket.pesenan;

import com.axell.microservices.common.webmodel.WebResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/pesenan")
public class PesenanRestController {

    @Autowired
    private PesenanService pesenanService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse placeOrder(@RequestBody OrderWebRequest orderWebRequest) {
        return WebResponse.OK(
                toOrderWebResponse(
                        pesenanService.placeNewOrder(
                                toOrderRequest(orderWebRequest)))
        );
    }

    private OrderRequest toOrderRequest(OrderWebRequest orderWebRequest) {
        OrderRequest orderRequest = new OrderRequest();
        BeanUtils.copyProperties(orderWebRequest, orderRequest);
        return orderRequest;
    }

    private OrderWebResponse toOrderWebResponse(OrderResponse orderResponse) {
        OrderWebResponse orderWebResponse = new OrderWebResponse();
        BeanUtils.copyProperties(orderResponse, orderWebResponse);
        return orderWebResponse;
    }

    @GetMapping(
            value = "/{orderId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<OrderDetailWebResponse> getOrderDetail(@PathVariable(value = "orderId") String orderId) {
        OrderDetailResponse orderDetailResponse = pesenanService.getOrderDetail(orderId);
        return WebResponse.OK(
                toOrderDetailWebResponse(orderDetailResponse)
        );
    }

    private OrderDetailWebResponse toOrderDetailWebResponse(OrderDetailResponse orderDetailResponse) {
        OrderDetailWebResponse orderDetailWebResponse = new OrderDetailWebResponse();
        BeanUtils.copyProperties(orderDetailResponse, orderDetailWebResponse);
        return orderDetailWebResponse;
    }

    @PostMapping(
            value = "/confirm/{orderId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<OrderWebResponse> confirmPayment(@PathVariable(value = "orderId") String orderId,
                                                        @RequestBody ConfirmPaymentWebRequest confirmPaymentWebRequest) {
        return WebResponse.OK(
                toOrderWebResponse(pesenanService.confirmPayment(orderId, toConfirmPaymentRequest(confirmPaymentWebRequest)))
        );
    }

    private ConfirmPaymentRequest toConfirmPaymentRequest(ConfirmPaymentWebRequest confirmPaymentWebRequest) {
        ConfirmPaymentRequest confirmPaymentRequest = new ConfirmPaymentRequest();
        BeanUtils.copyProperties(confirmPaymentWebRequest, confirmPaymentRequest);
        return confirmPaymentRequest;
    }
}
