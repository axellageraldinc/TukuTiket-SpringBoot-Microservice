package com.axell.tukutiket.ngirimtiket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class NgirimtiketMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(NgirimtiketMainApplication.class, args);
    }
}
