package com.axell.tukutiket.pesenan.acara;

import com.axell.microservices.common.webmodel.WebResponse;
import com.axell.tukutiket.pesenan.EventDetailResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "${acara.service.name}")
@RequestMapping(value = "/api/acara")
public interface AcaraProxy {
    @LoadBalanced
    @GetMapping(
            value = "/{eventId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EventDetailResponse> getEventDetail(@PathVariable(value = "eventId") String eventId);
}
