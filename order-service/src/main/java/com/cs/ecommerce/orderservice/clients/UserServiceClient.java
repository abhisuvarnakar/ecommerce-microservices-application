package com.cs.ecommerce.orderservice.clients;

import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import com.cs.ecommerce.sharedmodules.dto.user.UserProfileResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", path = "/users")
public interface UserServiceClient {

    @GetMapping("/api")
    ResponseEntity<ApiResponse<UserProfileResponseDTO>> getCurrentUser(
            @RequestHeader("X-User-Id") Long userId);

}
