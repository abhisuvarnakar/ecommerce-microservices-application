package com.cs.ecommerce.userservice.services;

import com.cs.ecommerce.userservice.dto.UpdateUserRequestDTO;
import com.cs.ecommerce.userservice.dto.UserAddressRequestDTO;
import com.cs.ecommerce.sharedmodules.dto.user.UserAddressResponseDTO;
import com.cs.ecommerce.sharedmodules.dto.user.UserProfileResponseDTO;
import com.cs.ecommerce.userservice.entities.User;

import java.util.List;

public interface UserService {

    User getUserById(Long userId);

    UserProfileResponseDTO getUserProfile(Long userId);

    UserProfileResponseDTO updateUser(Long userId, UpdateUserRequestDTO request);

    List<UserAddressResponseDTO> getUserAddresses(Long userId);

    Long addUserAddress(Long userId, UserAddressRequestDTO request);
}
