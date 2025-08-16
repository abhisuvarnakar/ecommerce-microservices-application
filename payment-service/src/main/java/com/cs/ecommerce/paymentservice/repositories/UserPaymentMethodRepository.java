package com.cs.ecommerce.paymentservice.repositories;

import com.cs.ecommerce.paymentservice.entities.UserPaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPaymentMethodRepository extends JpaRepository<UserPaymentMethod, Long> {

    List<UserPaymentMethod> findByUserId(Long userId);

    Optional<UserPaymentMethod> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);

    Optional<UserPaymentMethod> findByUserIdAndIsDefaultTrue(Long userId);

    Optional<UserPaymentMethod> findFirstByUserId(Long userId);
}
