package com.example.backend.config;

import com.example.backend.entity.UserEntity;
import com.example.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminAccountSeeder {

    @Bean
    CommandLineRunner createDefaultAdmin(UserRepository userRepository) {
        return args -> {
            String adminEmail = "admin@sidel.com";

            if (userRepository.existsByEmail(adminEmail)) {
                return;
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            UserEntity admin = new UserEntity();
            admin.setFirstname("sideL");
            admin.setLastname("Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(encoder.encode("admin123"));
            admin.setIsAdmin(true);
            admin.setIsProvider(false);
            admin.setProviderStatus("NONE");

            userRepository.save(admin);
        };
    }
}
