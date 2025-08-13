package com.cs.ecommerce.userservice.services;

import com.cs.ecommerce.userservice.dto.LoginRequestDTO;
import com.cs.ecommerce.userservice.dto.LoginResponseDTO;
import com.cs.ecommerce.userservice.dto.RegisterRequestDTO;
import com.cs.ecommerce.userservice.dto.RegisterResponseDTO;

public interface AuthService {

    RegisterResponseDTO registerUser(RegisterRequestDTO requestDTO);

    LoginResponseDTO loginUser(LoginRequestDTO requestDTO);
}
