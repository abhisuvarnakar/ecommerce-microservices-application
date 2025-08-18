package com.cs.ecommerce.sharedmodules.dto.user;

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
    private List<UserAddressResponseDTO> addresses;

}
