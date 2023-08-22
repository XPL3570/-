package com.confession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.confession")
public class ConfessionWallApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfessionWallApplication.class, args);
    }

}
