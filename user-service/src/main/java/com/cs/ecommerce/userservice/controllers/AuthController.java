package com.cs.ecommerce.userservice.controllers;

import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import com.cs.ecommerce.sharedmodules.dto.email.EmailNotificationRequestDTO;
import com.cs.ecommerce.userservice.dto.LoginRequestDTO;
import com.cs.ecommerce.userservice.dto.LoginResponseDTO;
import com.cs.ecommerce.userservice.dto.RegisterRequestDTO;
import com.cs.ecommerce.userservice.dto.RegisterResponseDTO;
import com.cs.ecommerce.userservice.producer.EmailProducer;
import com.cs.ecommerce.userservice.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailProducer emailProducer;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDTO>> registerUser(
            @Valid @RequestBody RegisterRequestDTO requestDTO) {

        RegisterResponseDTO responseDTO = authService.registerUser(requestDTO);
        EmailNotificationRequestDTO emailRequestDTO = EmailNotificationRequestDTO.builder()
                .to(requestDTO.getEmail())
                .subject("Welcome!")
                .template("REGISTRATION")
                .data(Map.of(
                        "userId", responseDTO.getUserId(),
                        "username", requestDTO.getEmail(),
                        "appName", "ECommerce App",
                        "supportEmail", "support@localhost.com",
                        "loginUrl", "http://localhost:8000/users/auth/login"
                )).build();
        emailProducer.sendEmailEvent(emailRequestDTO);
        return ResponseEntity.ok(ApiResponse.success(responseDTO, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> loginUser(
            @Valid @RequestBody LoginRequestDTO requestDTO) {

        LoginResponseDTO loginResponseDTO = authService.loginUser(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(loginResponseDTO, "User logged in " +
                "successfully"));

    }

}
