package com.cs.ecommerce.notificationservice.repository;

import com.cs.ecommerce.notificationservice.entities.Notification;
import com.cs.ecommerce.notificationservice.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId "
            + "AND (:type IS NULL OR n.type = :type) "
            + "AND (:read IS NULL OR ( :read = true AND n.status = 'READ') OR (:read = false AND n.status <> 'READ')) "
    )
    Page<Notification> findByUserIdAndFilters(
            @Param("userId") Long userId,
            @Param("type") NotificationType type,
            @Param("read") Boolean read,
            Pageable pageable
    );
}
