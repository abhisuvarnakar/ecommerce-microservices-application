package com.cs.ecommerce.paymentservice.repositories;

import com.cs.ecommerce.paymentservice.entities.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);

    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId ORDER BY p.createdAt DESC")
    Page<Payment> findByOrderIdPaged(@Param("orderId") Long orderId, Pageable pageable);

    Optional<Payment> findByIdAndOrderId(Long paymentId, Long orderId);
}
