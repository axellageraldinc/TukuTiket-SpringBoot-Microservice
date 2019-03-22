package com.axell.tukutiket.bayarbayar;

import com.axell.microservices.common.webmodel.WebResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/bayarbayar")
public class BayarbayarRestController {
    @Autowired
    private BayarbayarService bayarbayarService;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<BankWebResponse>> findAllBankInfo() {
        List<BankResponse> bankResponseList = bayarbayarService.findAllBankInfo();
        return WebResponse.OK(
                toBankWebResponseList(bankResponseList)
        );
    }

    private List<BankWebResponse> toBankWebResponseList(List<BankResponse> bankResponseList) {
        return bankResponseList
                .stream()
                .map(this::toBankWebResponse)
                .collect(Collectors.toList());
    }

    private BankWebResponse toBankWebResponse(BankResponse bankResponse) {
        BankWebResponse bankWebResponse = new BankWebResponse();
        BeanUtils.copyProperties(bankResponse, bankWebResponse);
        return bankWebResponse;
    }

    @GetMapping(
            value = "/{bankId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<BankDetailWebResponse> getBankDetail(@PathVariable(value = "bankId") String bankId) {
        BankDetailResponse bankDetailResponse = bayarbayarService.getBankDetail(bankId);
        return WebResponse.OK(
                toBankDetailWebResponse(bankDetailResponse)
        );
    }

    private BankDetailWebResponse toBankDetailWebResponse(BankDetailResponse bankDetailResponse) {
        BankDetailWebResponse bankDetailWebResponse = new BankDetailWebResponse();
        BeanUtils.copyProperties(bankDetailResponse, bankDetailWebResponse);
        return bankDetailWebResponse;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse validatePayment(@RequestBody PaymentWebRequest paymentWebRequest) {
        bayarbayarService.validatePayment(toPaymentRequest(paymentWebRequest));
        return WebResponse.OK();
    }

    private PaymentRequest toPaymentRequest(PaymentWebRequest paymentWebRequest) {
        PaymentRequest paymentRequest = new PaymentRequest();
        BeanUtils.copyProperties(paymentWebRequest, paymentRequest);
        return paymentRequest;
    }
}
