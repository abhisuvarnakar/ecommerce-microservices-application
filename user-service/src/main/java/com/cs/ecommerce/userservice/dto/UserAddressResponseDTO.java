package com.cs.ecommerce.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressResponseDTO {

    private Long id;
    private String addressLine;
    private String city;
    private String state;
    private Integer zipCode;
    private String country;
    private Boolean isDefault;
}
