package com.cs.ecommerce.notificationservice.dto;

import com.cs.ecommerce.sharedmodules.dto.Pagination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private List<NotificationDTO> notifications;
    private Pagination pagination;
}
