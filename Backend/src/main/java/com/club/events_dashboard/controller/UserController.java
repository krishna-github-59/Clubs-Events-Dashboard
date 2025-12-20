package com.club.events_dashboard.controller;

import com.club.events_dashboard.dto.ApiResponse;
import com.club.events_dashboard.entity.User;
import com.club.events_dashboard.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody User updatedUser){
        return userService.updateUser(id, updatedUser);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id){
        return userService.deleteUser(id);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
