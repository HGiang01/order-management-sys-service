package com.hoanggiang.order_service.services;

import com.hoanggiang.order_service.client.InventoryClient;
import com.hoanggiang.order_service.dto.OrderRequest;
import com.hoanggiang.order_service.event.OrderPlacedEvent;
import com.hoanggiang.order_service.models.Order;
import com.hoanggiang.order_service.repositories.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    // 1. Gắn Cầu dao điện vào hàm thực thi chính
    // Tên cầu dao là "inventoryCB" (bạn sẽ dùng tên này trong file properties)
    @CircuitBreaker(name = "inventoryServiceCircuitBreaker", fallbackMethod = "fallbackPlaceOrder")
    public Order placeOrder(OrderRequest orderRequest) {
        boolean isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if (isProductInStock) {
            Order newOrder = Order.builder()
                                  .orderNumber(UUID.randomUUID().toString())
                                  .skuCode(orderRequest.skuCode())
                                  .price(orderRequest.price())
                                  .quantity(orderRequest.quantity())
                                  .build();
            orderRepository.save(newOrder);

            // Kafka Producer
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(newOrder.getOrderNumber(), orderRequest.userDetails()
                                                                                                             .email(), orderRequest.userDetails()
                                                                                                                                   .firstName(), orderRequest.userDetails()
                                                                                                                                                             .lastName());
            log.info("Start sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);
            kafkaTemplate.sendDefault(newOrder.getOrderNumber(), orderPlacedEvent);
            log.info("OrderPlacedEvent sent to Kafka Topic");
            return newOrder;
        } else {
            throw new RuntimeException("The Product id: " + orderRequest.id() + " is out of stock");
        }
    }

    // 2. Định nghĩa hàm Fallback
    // - Cùng kiểu trả về: Order
    // - Cùng danh sách tham số: OrderRequest
    // - Nhận thêm 1 tham số Throwable ở cuối
    public Order fallbackPlaceOrder(OrderRequest orderRequest, Throwable throwable) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallback place order with SKU code: " + orderRequest.skuCode() + ", Caused: " + throwable.getMessage());
    }
}
