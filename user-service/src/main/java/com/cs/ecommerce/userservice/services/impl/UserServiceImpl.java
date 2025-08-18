package com.cs.ecommerce.userservice.services.impl;

import com.cs.ecommerce.sharedmodules.exceptions.ResourceNotFoundException;
import com.cs.ecommerce.userservice.dto.UpdateUserRequestDTO;
import com.cs.ecommerce.userservice.dto.UserAddressRequestDTO;
import com.cs.ecommerce.sharedmodules.dto.user.UserAddressResponseDTO;
import com.cs.ecommerce.sharedmodules.dto.user.UserProfileResponseDTO;
import com.cs.ecommerce.userservice.entities.User;
import com.cs.ecommerce.userservice.entities.UserAddress;
import com.cs.ecommerce.userservice.repositories.UserAddressRepository;
import com.cs.ecommerce.userservice.repositories.UserRepository;
import com.cs.ecommerce.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user =
                userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email: " + email + " not exists"));
        log.info("User ID: {}", user.getId());
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(
                "User with Id: " + userId + " not found."));
    }

    @Override
    public UserProfileResponseDTO getUserProfile(Long userId) {
        User user = getUserById(userId);
        return modelMapper.map(user, UserProfileResponseDTO.class);
    }

    @Override
    public UserProfileResponseDTO updateUser(Long userId, UpdateUserRequestDTO request) {
        User user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserProfileResponseDTO.class);
    }

    @Override
    public List<UserAddressResponseDTO> getUserAddresses(Long userId) {
        List<UserAddress> addresses = userAddressRepository.findByUserId(userId);
        return addresses.stream()
                .map(address -> modelMapper.map(address, UserAddressResponseDTO.class))
                .toList();
    }

    @Override
    public Long addUserAddress(Long userId, UserAddressRequestDTO request) {
        User user = getUserById(userId);
        UserAddress address = modelMapper.map(request, UserAddress.class);
        address.setUser(user);

        UserAddress savedAddress = userAddressRepository.save(address);
        return savedAddress.getId();
    }
}
