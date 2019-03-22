package com.axell.tukutiket.pesenan.bayarbayar;

import com.axell.microservices.common.webmodel.WebResponse;
import com.axell.tukutiket.pesenan.ConfirmPaymentRequest;
import com.axell.tukutiket.pesenan.PaymentDetailResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "${bayarbayar.service.name}")
@RequestMapping(value = "/api/bayarbayar")
public interface BayarbayarProxy {
    @LoadBalanced
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse validatePayment(@RequestBody ConfirmPaymentRequest confirmPaymentRequest);

    @LoadBalanced
    @GetMapping(
            value = "/{bankId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<PaymentDetailResponse> getPaymentDetail(@PathVariable(value = "bankId") String bankId);
}
