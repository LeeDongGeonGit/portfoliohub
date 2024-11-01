package com.example.portfoliohubback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PortfoliohubbackApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfoliohubbackApplication.class, args);
    }

}
