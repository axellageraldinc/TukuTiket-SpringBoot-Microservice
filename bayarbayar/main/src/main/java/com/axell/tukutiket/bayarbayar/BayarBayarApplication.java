package com.axell.tukutiket.bayarbayar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class BayarBayarApplication {
    public static void main(String[] args) {
        SpringApplication.run(BayarBayarApplication.class, args);
    }
}
