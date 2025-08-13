package com.cs.ecommerce.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponseDTO {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private List<UserAddressResponseDTO> addresses;

}
