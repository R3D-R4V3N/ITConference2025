package com.hogent.ewdj.itconference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hogent.ewdj.itconference", "domain", "exceptions", "repository", "service", "util", "validator", "com.hogent.ewdj.itconference.advice", "com.hogent.ewdj.itconference.config", "com.hogent.ewdj.itconference.controller"})
@EnableJpaRepositories("repository")
@EntityScan("domain")
public class ItConferenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItConferenceApplication.class, args);
    }

}