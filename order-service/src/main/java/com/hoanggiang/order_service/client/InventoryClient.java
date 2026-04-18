package com.hoanggiang.order_service.client;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/v1/inventory")
public interface InventoryClient {
    @GetExchange()
    boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);
}
