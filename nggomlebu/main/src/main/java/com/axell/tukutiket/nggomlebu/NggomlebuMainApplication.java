package com.axell.tukutiket.nggomlebu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class NggomlebuMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(NggomlebuMainApplication.class, args);
    }
}
