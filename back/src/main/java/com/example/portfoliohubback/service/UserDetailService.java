package com.example.portfoliohubback.service;

import com.example.portfoliohubback.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.example.portfoliohubback.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserEntity loadUserByUsername(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id));
    }
}
