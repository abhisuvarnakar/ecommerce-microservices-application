package com.cs.ecommerce.inventoryservice.service;

import com.cs.ecommerce.inventoryservice.dto.*;
import com.cs.ecommerce.sharedmodules.dto.inventory.InventoryResponseDTO;
import com.cs.ecommerce.sharedmodules.dto.inventory.ReserveStockRequestDTO;
import com.cs.ecommerce.sharedmodules.dto.inventory.ReserveStockResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface InventoryService {

    InventoryResponseDTO getInventory(Long productId);

    StockUpdateResponseDTO updateStock(Long userId, Long productId,
                                       StockUpdateRequestDTO request);

    ReserveStockResponseDTO reserveStock(Long userId, ReserveStockRequestDTO request);

    String releaseStock(Long userId, Long reservationId);

    List<LowStockProductDTO> getLowStockProducts(Integer threshold);

    List<StockMovementDTO> getStockMovements(Long productId, LocalDate startDate,
                                             LocalDate endDate);

    StockAdjustmentResponseDTO adjustStock(Long userId, StockAdjustmentRequestDTO request);

    List<ReserveStockResponseDTO> bulkReserveStock(Long userId, List<ReserveStockRequestDTO> requests);

    String releaseStockFromOrder(Long userId, Long orderId);
}
