package com.vahabvahabov.PaySnap.service.impl;

import com.vahabvahabov.PaySnap.dto.AuthRequest;
import com.vahabvahabov.PaySnap.dto.AuthResponse;
import com.vahabvahabov.PaySnap.model.User;
import com.vahabvahabov.PaySnap.repository.UserRepository;
import com.vahabvahabov.PaySnap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void saveUser(AuthRequest authRequest) {
        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    public boolean authentication(String username, String password) {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) {
            return false;
        }
        User dbUser = optional.get();
        return passwordEncoder.matches(password, dbUser.getPassword());

    }


}
