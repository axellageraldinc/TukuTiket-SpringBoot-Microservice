package com.axell.tukutiket.uwong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UwongMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(UwongMainApplication.class, args);
    }
}
