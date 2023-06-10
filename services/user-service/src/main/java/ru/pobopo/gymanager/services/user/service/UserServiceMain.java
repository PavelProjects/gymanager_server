package ru.pobopo.gymanager.services.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.pobopo.gymanager.services.user.service.services.api.UserService;
import ru.pobopo.gymanager.services.user.service.controller.objects.CreateUserRequest;

@Slf4j
@SpringBootApplication
public class UserServiceMain {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceMain.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService) {
        return args -> {
            CreateUserRequest createUserRequest = new CreateUserRequest();
            createUserRequest.setLogin("autotest");
            createUserRequest.setName("Autotest user");
            createUserRequest.setEmail("test@emai.com");
            createUserRequest.setType("staff");
            createUserRequest.setPassword("123");
            try {
                userService.createUser(createUserRequest);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        };
    }
}
