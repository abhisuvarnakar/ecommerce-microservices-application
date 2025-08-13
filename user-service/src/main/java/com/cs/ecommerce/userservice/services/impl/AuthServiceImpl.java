package com.cs.ecommerce.userservice.services.impl;

import com.cs.ecommerce.userservice.dto.LoginRequestDTO;
import com.cs.ecommerce.userservice.dto.LoginResponseDTO;
import com.cs.ecommerce.userservice.dto.RegisterRequestDTO;
import com.cs.ecommerce.userservice.dto.RegisterResponseDTO;
import com.cs.ecommerce.userservice.entities.User;
import com.cs.ecommerce.userservice.repositories.UserRepository;
import com.cs.ecommerce.userservice.security.JwtService;
import com.cs.ecommerce.userservice.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public RegisterResponseDTO registerUser(RegisterRequestDTO requestDTO) {
        userRepository.findByEmail(requestDTO.getEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("User with email: " + requestDTO.getEmail() + " " +
                            "already exists");
                });

        User user = modelMapper.map(requestDTO, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        return RegisterResponseDTO.builder()
                .userId(savedUser.getId())
                .message("User registered successfully")
                .build();
    }

    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO requestDTO) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDTO.getEmail(), requestDTO.getPassword()));
        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResponseDTO(user.getId(), accessToken);
    }
}
