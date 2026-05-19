package com.example.backend.service;
import com.example.backend.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {
    public UserEntity createNewUser(UserEntity input) {
        UserEntity user = new UserEntity();
        user.setFirstname(input.getFirstname());
        user.setLastname(input.getLastname());
        user.setEmail(input.getEmail());
        user.setPassword(input.getPassword());
        user.setPhoneNumber(input.getPhoneNumber());
        user.setAddress(input.getAddress());
        user.setBio(input.getBio());
        return user;
    }
}
