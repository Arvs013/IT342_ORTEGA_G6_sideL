package com.example.backend.controller;

import com.example.backend.entity.UserEntity;
import com.example.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
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
}
