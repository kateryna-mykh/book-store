package bookstore.service;

import bookstore.dto.OrderAddressDto;
import bookstore.dto.OrderDto;
import bookstore.dto.OrderItemDto;
import bookstore.dto.OrderStatusDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    List<OrderDto> getAll(Pageable pageable);

    OrderDto save(OrderAddressDto shippingAddress);
    
    OrderDto updateStatus(Long orderId, OrderStatusDto status);
    
    List<OrderItemDto> getOrderItems(Long orderId);

    OrderItemDto getOrderItem(Long orderId, Long itemId);
}
