package com.example.it210ticketbus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.it210ticketbus")
@EnableScheduling
@EnableAsync
public class It210TicketBusApplication {

    public static void main(String[] args) {
        SpringApplication.run(It210TicketBusApplication.class, args);
    }

}
