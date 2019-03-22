package com.axell.tukutiket.acara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class AcaraApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcaraApplication.class, args);
    }

}
