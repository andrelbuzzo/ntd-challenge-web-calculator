package com.ntd.webcalculator;

import com.ntd.webcalculator.enums.Role;
import com.ntd.webcalculator.user.User;
import com.ntd.webcalculator.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebCalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebCalculatorApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService) {
        return args -> {
            String defaultPswd = "123456";

            if (userService.findByUsername("admin@ntd.com").isEmpty()) {
                userService.save(new User("Admin", "admin@ntd.com", defaultPswd, Role.ADMIN));
            }
            for (int i = 1; i <= 10; i++) {
                String username = "user" + i + "@ntd.com";
                if (userService.findByUsername(username).isEmpty()) {
                    userService.save(new User("User " + i, username, defaultPswd, null));
                }
            }
        };
    }
}
