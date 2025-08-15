package com.cs.ecommerce.orderservice.repository;

import com.cs.ecommerce.orderservice.entities.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

    List<OrderStatusHistory> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.order.id IN :orderIds")
    List<OrderStatusHistory> findByOrderIds(@Param("orderIds") List<Long> orderIds);
}
