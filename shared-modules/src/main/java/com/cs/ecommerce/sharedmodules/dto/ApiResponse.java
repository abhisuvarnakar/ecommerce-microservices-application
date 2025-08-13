package com.cs.ecommerce.sharedmodules.dto;

import com.cs.ecommerce.sharedmodules.enums.ApiStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private ApiStatus status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(ApiStatus.SUCCESS, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return success(null, message);
    }

    public static <T> ApiResponse<T> error(T data, String message) {
        return new ApiResponse<>(ApiStatus.ERROR, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(null, message);
    }
}
