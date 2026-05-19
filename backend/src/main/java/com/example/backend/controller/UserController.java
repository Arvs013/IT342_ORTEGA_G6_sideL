package com.example.backend.controller;

import com.example.backend.entity.UserEntity;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final UserRepository repository;

    public UserController(UserService service, UserRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @GetMapping
    public List<UserEntity> getAll() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserEntity getById(@PathVariable Integer id) {
        return service.getUserById(id);
    }

    // REGISTER
    @PostMapping("/register")
    public UserEntity register(@RequestBody UserEntity user) {
        return service.saveUser(user);
    }

    // LOGIN
    @PostMapping("/login")
    public UserEntity login(@RequestBody UserEntity user) {
        return service.login(user.getEmail(), user.getPassword());
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
            return ResponseEntity.ok(repository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }
}
