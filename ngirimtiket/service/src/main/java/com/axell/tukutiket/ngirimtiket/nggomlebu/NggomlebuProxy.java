package com.axell.tukutiket.ngirimtiket.nggomlebu;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${nggomlebu.service.name}")
@RequestMapping(value = "/api/nggomlebu")
public interface NggomlebuProxy {
    @LoadBalanced
    @GetMapping(
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public Resource downloadQRCodeImage(@RequestParam(value = "name") String imageName);
}
