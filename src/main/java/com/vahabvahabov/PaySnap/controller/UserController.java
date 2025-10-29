package com.vahabvahabov.PaySnap.controller;

import com.vahabvahabov.PaySnap.dto.AuthRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public interface UserController {

    public ResponseEntity<?> login(AuthRequest request, BindingResult result);

    public ResponseEntity<?> register(AuthRequest authRequest, BindingResult bindingResult);

    public ResponseEntity<?> logout(String authHeader);
}
