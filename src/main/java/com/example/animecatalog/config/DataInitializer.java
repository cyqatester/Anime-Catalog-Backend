package com.example.animecatalog.config;

import com.example.animecatalog.entity.*;
import com.example.animecatalog.repository.*;
import com.example.animecatalog.service.PasswordService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(UserRepository users, PasswordService passwords) {
        return args -> {
            if (!users.existsByUsername("superadmin"))
                users.save(new UserAccount("Super Administrator", "superadmin", passwords.hash("ChangeMe123!"),
                        UserRole.ADMIN, true));
        };
    }
}
