package com.example.loomweb.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.loomweb.model.AuthRequest;

@RestController
@RequestMapping("/api")
public class AuthController {

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        // TODO: Authenticate user, generate JWT, etc.
        // For now, just return a dummy token for testing:
        return "{\"token\": \"dummy-jwt-token\"}";
    }

    @PostMapping("/signup")
    public String signup(@RequestBody AuthRequest request) {
        // TODO: Register user, generate JWT, etc.
        return "{\"token\": \"dummy-jwt-token\"}";
    }
}