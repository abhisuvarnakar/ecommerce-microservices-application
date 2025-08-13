package com.cs.ecommerce.inventoryservice.controller;

import com.cs.ecommerce.inventoryservice.dto.*;
import com.cs.ecommerce.inventoryservice.service.InventoryService;
import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> getInventory(
            @PathVariable("productId") Long productId) {
        InventoryResponseDTO response = inventoryService.getInventory(productId);
        return ResponseEntity.ok(ApiResponse.success(response,
                "Inventory fetched successfully"));
    }

    @PutMapping("/{productId}/stock")
    public ResponseEntity<ApiResponse<StockUpdateResponseDTO>> updateStock(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("productId") Long productId,
            @Valid @RequestBody StockUpdateRequestDTO request) {
        StockUpdateResponseDTO response = inventoryService.updateStock(userId, productId, request);
        return ResponseEntity.ok(ApiResponse.success(response,
                "Stock updated successfully"));
    }

    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<ReserveStockResponseDTO>> reserveStock(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ReserveStockRequestDTO request) {
        ReserveStockResponseDTO response = inventoryService.reserveStock(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response,
                "Stock reserved successfully"));
    }

    @PostMapping("/release/{reservationId}")
    public ResponseEntity<ApiResponse<String>> releaseStock(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("reservationId") Long reservationId) {
        String response = inventoryService.releaseStock(userId, reservationId);
        return ResponseEntity.ok(ApiResponse.success(response,
                "Stock released successfully"));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<LowStockProductDTO>>> getLowStockProducts(
            @RequestParam(name = "threshold", required = false) Integer threshold) {
        List<LowStockProductDTO> response = inventoryService.getLowStockProducts(threshold);
        return ResponseEntity.ok(ApiResponse.success(response,
                "Low stock products fetched successfully"));
    }

    @GetMapping("/movements/{productId}")
    public ResponseEntity<ApiResponse<List<StockMovementDTO>>> getStockMovements(
            @PathVariable("productId") Long productId,
            @RequestParam(name = "startDate", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<StockMovementDTO> response = inventoryService.getStockMovements(productId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response,
                "Stock movements fetched successfully"));
    }

    @PostMapping("/adjustment")
    public ResponseEntity<ApiResponse<StockAdjustmentResponseDTO>> adjustStock(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody StockAdjustmentRequestDTO request) {
        StockAdjustmentResponseDTO response = inventoryService.adjustStock(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response,
                "Stock adjusted successfully"));
    }

}
