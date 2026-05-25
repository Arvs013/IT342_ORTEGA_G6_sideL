package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.entity.UserEntity;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtService;
import com.example.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final UserRepository repository;
    private final JwtService jwtService;

    public UserController(UserService service, UserRepository repository, JwtService jwtService) {
        this.service = service;
        this.repository = repository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public List<UserEntity> getAll() {
        return service.getAllUsers().stream().map(this::withoutPassword).toList();
    }

    @GetMapping("/{id}")
    public UserEntity getById(@PathVariable Integer id) {
        return withoutPassword(service.getUserById(id));
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserEntity user) {
        try {
            return ResponseEntity.ok(withoutPassword(service.saveUser(user)));
        } catch (RuntimeException err) {
            return ResponseEntity.badRequest().body(err.getMessage());
        }
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserEntity user) {
        try {
            UserEntity loggedUser = service.login(user.getEmail(), user.getPassword());
            return ResponseEntity.ok(new AuthResponse(jwtService.generateToken(loggedUser), withoutPassword(loggedUser)));
        } catch (RuntimeException err) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Integer id, @RequestBody UserEntity input) {
        return repository.findById(id).map(user -> {
            user.setFirstname(input.getFirstname());
            user.setLastname(input.getLastname());
            user.setEmail(input.getEmail());
            user.setPhoneNumber(input.getPhoneNumber());
            user.setAddress(input.getAddress());
            user.setBio(input.getBio());
            if (input.getProfileImageUrl() != null) {
                user.setProfileImageUrl(input.getProfileImageUrl());
            }
            return ResponseEntity.ok(withoutPassword(repository.save(user)));
        }).orElse(ResponseEntity.notFound().build());
    }

    private UserEntity withoutPassword(UserEntity user) {
        UserEntity safeUser = new UserEntity();
        safeUser.setUserID(user.getUserID());
        safeUser.setFirstname(user.getFirstname());
        safeUser.setLastname(user.getLastname());
        safeUser.setEmail(user.getEmail());
        safeUser.setPhoneNumber(user.getPhoneNumber());
        safeUser.setAddress(user.getAddress());
        safeUser.setBio(user.getBio());
        safeUser.setProfileImageUrl(user.getProfileImageUrl());
        safeUser.setIsAdmin(user.getIsAdmin());
        safeUser.setIsProvider(user.getIsProvider());
        safeUser.setProviderStatus(user.getProviderStatus());
        safeUser.setAccountStatus(user.getAccountStatus());
        safeUser.setCreatedAT(user.getCreatedAT());
        return safeUser;
    }
}
