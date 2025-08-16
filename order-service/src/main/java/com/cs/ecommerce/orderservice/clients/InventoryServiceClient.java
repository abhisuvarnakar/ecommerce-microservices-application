package com.cs.ecommerce.orderservice.clients;

import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import com.cs.ecommerce.sharedmodules.dto.inventory.InventoryResponseDTO;
import com.cs.ecommerce.sharedmodules.dto.inventory.ReserveStockRequestDTO;
import com.cs.ecommerce.sharedmodules.dto.inventory.ReserveStockResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "inventory-service", path = "/inventory")
public interface InventoryServiceClient {

    @GetMapping("/api/{productId}")
    ResponseEntity<ApiResponse<InventoryResponseDTO>> getInventory(@PathVariable("productId") Long productId);

    @PostMapping("/api/reserve")
    ResponseEntity<ApiResponse<ReserveStockResponseDTO>> reserveStock(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ReserveStockRequestDTO request);

    @PostMapping("/api/bulk-reserve")
    ResponseEntity<ApiResponse<List<ReserveStockResponseDTO>>> bulkReserveStock(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<ReserveStockRequestDTO> requests);

    @PostMapping("/api/release/order/{orderId}")
    public ResponseEntity<ApiResponse<String>> releaseStockFromOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("orderId") Long orderId);

}
