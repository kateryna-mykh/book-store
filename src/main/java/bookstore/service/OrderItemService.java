package bookstore.service;

import bookstore.dto.OrderItemDto;
import bookstore.model.CartItem;
import bookstore.model.Order;
import bookstore.model.OrderItem;
import bookstore.model.ShoppingCart;
import java.util.List;
import java.util.Set;

public interface OrderItemService {
    List<OrderItemDto> getItemsByOrderId(Long orderId);

    OrderItemDto getItemById(Long itemId);
    
    OrderItemDto getItemByOrderId(Long orderId, Long itemId);
    
    OrderItem save(CartItem cartItem, Order order);
    
    Set<OrderItem> saveAll(ShoppingCart shoppingCart, Order order);
}
