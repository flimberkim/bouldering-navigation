package com.boulderingnavigation.service;

import com.boulderingnavigation.domain.User;
import com.boulderingnavigation.dto.UserResponse;
import com.boulderingnavigation.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthQueryService {

    private final UserRepository userRepository;

    public UserResponse me(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname());
    }
}
