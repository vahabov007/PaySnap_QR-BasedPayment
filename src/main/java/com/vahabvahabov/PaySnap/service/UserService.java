package com.vahabvahabov.PaySnap.service;

import com.vahabvahabov.PaySnap.dto.AuthRequest;
import com.vahabvahabov.PaySnap.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);

    void saveUser(AuthRequest authRequest);

    boolean authentication(String username, String password);


}
