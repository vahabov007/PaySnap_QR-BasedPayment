package com.vahabvahabov.PaySnap.controller.impl;

import com.beust.ah.A;
import com.vahabvahabov.PaySnap.config.jwt.JwtUtil;
import com.vahabvahabov.PaySnap.controller.UserController;
import com.vahabvahabov.PaySnap.dto.AuthRequest;
import com.vahabvahabov.PaySnap.dto.AuthResponse;
import com.vahabvahabov.PaySnap.model.User;
import com.vahabvahabov.PaySnap.repository.BlackListRepository;
import com.vahabvahabov.PaySnap.repository.UserRepository;
import com.vahabvahabov.PaySnap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserControllerImpl implements UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BlackListRepository blackListRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            final String jwt = jwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponse(jwt, request.getUsername(), "Login Successfully."));


        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, request.getUsername(), "Login Unsuccessfully. Invalid Credentials."));
        }
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest authRequest, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
                return ResponseEntity.badRequest().body(errors);
            }
        }
        Optional<User> optional = userRepository.findByUsername(authRequest.getUsername());
        if (optional.isPresent()) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, authRequest.getUsername(), "Username already exists."));
        }
        userService.saveUser(authRequest);
        return ResponseEntity.ok("The user registered successfully.");
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.substring(7);
            long expirationTime = jwtUtil.getExpirationTime(token).getTime() - System.currentTimeMillis();
            if (expirationTime > 0) {
                blackListRepository.blackListToken(token, expirationTime);
            }
        }
        return ResponseEntity.ok("Logged out successfully.");
    }


}
