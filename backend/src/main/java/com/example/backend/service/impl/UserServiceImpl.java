package com.example.backend.service.impl;

import com.example.backend.entity.UserEntity;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public UserEntity getUserById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserEntity saveUser(UserEntity user) {

        // check if email already exists
        if (repository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // encrypt password
        user.setPassword(encoder.encode(user.getPassword()));

        return repository.save(user);
    }

    @Override
    public UserEntity login(String email, String password) {

        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("DISABLED".equalsIgnoreCase(user.getAccountStatus())) {
            throw new RuntimeException("This account has been disabled by admin.");
        }

        // compare encrypted password
        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }
}
