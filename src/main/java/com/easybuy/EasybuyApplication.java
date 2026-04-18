package com.easybuy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.easybuy.repository")
@EntityScan(basePackages = "com.easybuy.entity")
public class EasybuyApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasybuyApplication.class, args);
    }
}
