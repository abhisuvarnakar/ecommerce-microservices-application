package com.cs.ecommerce.paymentservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "order-service", path = "/orders")
public interface OrderServiceClient {

    @GetMapping("/api/{orderId}")
    String getOrderDetails(@PathVariable("orderId") Long orderId,
            @RequestHeader("X-User-Id") Long userId);
}
