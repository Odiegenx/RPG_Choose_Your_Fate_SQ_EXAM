package com.example.chooseyourfatebackend;

import dk.ek.gruppe2.chooseyourfate.ChooseYourFateBackendApplication;
import org.springframework.boot.SpringApplication;

public class TestChooseYourFateBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(ChooseYourFateBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
