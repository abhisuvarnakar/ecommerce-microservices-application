package com.cs.ecommerce.orderservice.dto;

import com.cs.ecommerce.sharedmodules.dto.Pagination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedOrderResponse {
    private List<OrderDTO> orders;
    private Pagination pagination;
}
