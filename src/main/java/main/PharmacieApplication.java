package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"main", "config", "controller"})
public class PharmacieApplication {

    public static void main(String[] args) {
        SpringApplication.run(PharmacieApplication.class, args);
    }
}