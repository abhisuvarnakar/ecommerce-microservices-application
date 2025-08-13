package com.cs.ecommerce.inventoryservice.service.impl;

import com.cs.ecommerce.inventoryservice.dto.*;
import com.cs.ecommerce.inventoryservice.entities.Inventory;
import com.cs.ecommerce.inventoryservice.entities.StockMovement;
import com.cs.ecommerce.inventoryservice.entities.StockReservation;
import com.cs.ecommerce.inventoryservice.enums.StockMovementType;
import com.cs.ecommerce.inventoryservice.enums.StockReservationStatus;
import com.cs.ecommerce.inventoryservice.exceptions.InsufficientStockException;
import com.cs.ecommerce.inventoryservice.exceptions.InventoryNotFoundException;
import com.cs.ecommerce.inventoryservice.exceptions.ReservationNotFoundException;
import com.cs.ecommerce.inventoryservice.repository.InventoryRepository;
import com.cs.ecommerce.inventoryservice.repository.StockMovementRepository;
import com.cs.ecommerce.inventoryservice.repository.StockReservationRepository;
import com.cs.ecommerce.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockReservationRepository stockReservationRepository;
    private final ModelMapper modelMapper;

    @Override
    public InventoryResponseDTO getInventory(Long productId) {
        Inventory inventory = getInventoryByProductId(productId);

        return modelMapper.map(inventory, InventoryResponseDTO.class);
    }

    @Override
    public StockUpdateResponseDTO updateStock(Long userId, Long productId,
                                              StockUpdateRequestDTO request) {
        Inventory inventory;
        try {
            inventory = getInventoryByProductId(productId);
        } catch (InventoryNotFoundException e) {
            inventory = new Inventory();
        }

        if (inventory.getId() == null) {
            inventory.setProductId(productId);
        }

        Integer newTotalStock;
        String operation = request.getOperation();

        if ("ADD".equalsIgnoreCase(operation)) {
            newTotalStock = inventory.getTotalStock() + request.getQuantity();
        } else {
            newTotalStock = request.getQuantity();
        }

        Integer totalStock = inventory.getTotalStock() != null ? inventory.getTotalStock() : 0;
        Integer stockDifference = newTotalStock - totalStock;
        Integer availableStock = inventory.getAvailableStock() != null ?
                inventory.getAvailableStock() : 0;
        Integer newAvailableStock = Math.max(0, availableStock + stockDifference);

        inventory.setTotalStock(newTotalStock);
        inventory.setAvailableStock(newAvailableStock);

        inventoryRepository.save(inventory);

        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .movementType("ADD".equalsIgnoreCase(operation) ? StockMovementType.STOCK_IN :
                        StockMovementType.ADJUSTMENT)
                .quantity(request.getQuantity())
                .reason("Stock " + operation.toLowerCase() + " operation")
                .createUserId(userId)
                .build();
        stockMovementRepository.save(movement);

        log.info("Stock updated for product {}: new total stock = {}", productId, newTotalStock);

        return new StockUpdateResponseDTO("Stock updated successfully", newTotalStock);
    }

    @Override
    public ReserveStockResponseDTO reserveStock(Long userId, ReserveStockRequestDTO request) {
        Inventory inventory = getInventoryByProductId(request.getProductId());

        if (inventory.getAvailableStock() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock. Available: " +
                    inventory.getAvailableStock() + ", Requested: " + request.getQuantity());
        }

        StockReservation reservation = StockReservation.builder()
                .productId(request.getProductId())
                .orderId(request.getOrderId())
                .quantity(request.getQuantity())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .status(StockReservationStatus.ACTIVE)
                .build();

        reservation = stockReservationRepository.save(reservation);

        inventory.setAvailableStock(inventory.getAvailableStock() - request.getQuantity());
        inventory.setReservedStock(inventory.getReservedStock() + request.getQuantity());
        inventoryRepository.save(inventory);

        StockMovement movement = StockMovement.builder()
                .productId(request.getProductId())
                .movementType(StockMovementType.RESERVATION)
                .quantity(request.getQuantity())
                .reason("Stock reserved for order: " + request.getOrderId())
                .createUserId(userId)
                .build();
        stockMovementRepository.save(movement);

        // TODO: Set expiration timer

        return new ReserveStockResponseDTO(reservation.getId(), reservation.getExpiresAt());
    }

    @Override
    public String releaseStock(Long userId, Long reservationId) {
        StockReservation reservation = stockReservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(
                        "Reservation not found: " + reservationId));

        if (reservation.getStatus() != StockReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation is not active");
        }

        Inventory inventory = getInventoryByProductId(reservation.getProductId());
        reservation.setStatus(StockReservationStatus.RELEASED);
        stockReservationRepository.save(reservation);

        inventory.setAvailableStock(inventory.getAvailableStock() + reservation.getQuantity());
        inventory.setReservedStock(inventory.getReservedStock() - reservation.getQuantity());
        inventoryRepository.save(inventory);

        StockMovement movement = StockMovement.builder()
                .productId(reservation.getProductId())
                .movementType(StockMovementType.RELEASE)
                .quantity(reservation.getQuantity())
                .reason("Stock released from reservation: " + reservationId)
                .createUserId(userId)
                .build();
        stockMovementRepository.save(movement);

        return "Stock released successfully";
    }

    @Override
    public List<LowStockProductDTO> getLowStockProducts(Integer threshold) {
        List<Inventory> lowStockInventories = inventoryRepository.findLowStockProducts(threshold);
        return lowStockInventories.stream()
                .map(inventory -> {
                    LowStockProductDTO dto = modelMapper.map(inventory, LowStockProductDTO.class);
                    dto.setCurrentStock(inventory.getAvailableStock());
                    return dto;
                })
                .toList();
    }

    @Override
    public List<StockMovementDTO> getStockMovements(Long productId, LocalDate startDate,
                                                    LocalDate endDate) {
        Timestamp startDateTime = startDate != null ?
                Timestamp.valueOf(startDate.atStartOfDay()) : null;
        Timestamp endDateTime = endDate != null ? Timestamp.valueOf(endDate.atTime(23, 59, 59)) :
                null;

        List<StockMovement> movements =
                stockMovementRepository.findMovementsByProductAndDateRange(productId,
                        startDateTime, endDateTime);

        return movements.stream()
                .map(movement -> modelMapper.map(movement, StockMovementDTO.class))
                .toList();
    }

    @Override
    public StockAdjustmentResponseDTO adjustStock(Long userId, StockAdjustmentRequestDTO request) {
        Inventory inventory = getInventoryByProductId(request.getProductId());
        StockMovementType movementType = switch (request.getType()) {
            case "DAMAGE" -> StockMovementType.DAMAGE;
            case "THEFT" -> StockMovementType.THEFT;
            case "RECOUNT" -> StockMovementType.RECOUNT;
            default -> StockMovementType.ADJUSTMENT;
        };

        Integer adjustment = request.getQuantity();
        if (("DAMAGE".equals(request.getType())
                || "THEFT".equals(request.getType())) && adjustment > 0) {
            adjustment = -adjustment;
        }

        Integer newTotalStock = Math.max(0, inventory.getTotalStock() + adjustment);
        Integer newAvailableStock = Math.max(0, inventory.getAvailableStock() + adjustment);

        inventory.setTotalStock(newTotalStock);
        inventory.setAvailableStock(newAvailableStock);
        inventoryRepository.save(inventory);

        StockMovement movement = StockMovement.builder()
                .productId(request.getProductId())
                .movementType(movementType)
                .quantity(Math.abs(adjustment))
                .reason(request.getReason())
                .createUserId(userId)
                .build();
        StockMovement savedMovement = stockMovementRepository.save(movement);

        // TODO: Create audit log entry

        return new StockAdjustmentResponseDTO(savedMovement.getId(),
                "Stock adjustment recorded successfully");
    }

    private Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product ID: " + productId));
    }
}
