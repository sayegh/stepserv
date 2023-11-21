package com.thinkboxberlin.stepserv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class Application {
    /**
     * Kooks main starter.
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

