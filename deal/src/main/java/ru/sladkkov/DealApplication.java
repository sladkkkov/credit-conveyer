package ru.sladkkov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DealApplication {
  public static void main(String[] args) {
    SpringApplication.run(DealApplication.class, args);
  }
}
