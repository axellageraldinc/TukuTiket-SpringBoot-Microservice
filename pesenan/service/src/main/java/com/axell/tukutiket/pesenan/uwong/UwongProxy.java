package com.axell.tukutiket.pesenan.uwong;

import com.axell.microservices.common.webmodel.WebResponse;
import com.axell.tukutiket.pesenan.UserDetailResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "${uwong.service.name}")
@RequestMapping(value = "/api/uwong")
public interface UwongProxy {
    @LoadBalanced
    @GetMapping(
            value = "/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserDetailResponse> getUserDetail(@PathVariable(value = "userId") String userId);
}
