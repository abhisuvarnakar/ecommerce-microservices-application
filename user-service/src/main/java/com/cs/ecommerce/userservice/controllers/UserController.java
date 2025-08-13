package com.cs.ecommerce.userservice.controllers;

import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import com.cs.ecommerce.userservice.dto.UpdateUserRequestDTO;
import com.cs.ecommerce.userservice.dto.UserAddressRequestDTO;
import com.cs.ecommerce.userservice.dto.UserAddressResponseDTO;
import com.cs.ecommerce.userservice.dto.UserProfileResponseDTO;
import com.cs.ecommerce.userservice.security.JwtService;
import com.cs.ecommerce.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> getCurrentUser(
            @RequestHeader("X-User-Id") Long userId) {
        UserProfileResponseDTO profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile, "User profile fetched successfully"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> updateCurrentUser(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateUserRequestDTO request) {
        UserProfileResponseDTO updatedUser = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "User updated successfully"));
    }

    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<UserAddressResponseDTO>>> getCurrentUserAddresses(
            @RequestHeader("X-User-Id") Long userId) {
        List<UserAddressResponseDTO> addresses = userService.getUserAddresses(userId);
        return ResponseEntity.ok(ApiResponse.success(addresses, "User addresses fetched successfully"));
    }

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addAddressToCurrentUser(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserAddressRequestDTO request) {
        Long addressId = userService.addUserAddress(userId, request);
        Map<String, Object> responseData = Map.of("addressId", addressId);
        return ResponseEntity.ok(ApiResponse.success(responseData, "Address added successfully"));
    }
}
