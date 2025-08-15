package com.cs.ecommerce.sharedmodules.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveStockResponseDTO {
    private Long reservationId;
    private LocalDateTime expiresAt;
}
