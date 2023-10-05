package bookstore.service.impl;

import bookstore.dto.OrderItemDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.OrderItemMapper;
import bookstore.model.CartItem;
import bookstore.model.Order;
import bookstore.model.OrderItem;
import bookstore.model.ShoppingCart;
import bookstore.repository.OrderItemRepository;
import bookstore.service.OrderItemService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper itemMapper;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository,
            OrderItemMapper itemMapper) {
        this.orderItemRepository = orderItemRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public List<OrderItemDto> getItemsByOrderId(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId).stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getItemById(Long itemId) {
        return itemMapper.toDto(orderItemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException("Can't get order item by id " + itemId)));
    }
    
    @Transactional
    @Override
    public Set<OrderItem> saveAll(ShoppingCart shoppingCart, Order order) {
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        return cartItems.stream()
                .map(item -> save(item, order))
                .collect(Collectors.toSet());
    }

    @Override
    public OrderItem save(CartItem cartItem, Order order) {
        return orderItemRepository.save(itemMapper.toModel(cartItem, order));
    }

    @Override
    public OrderItemDto getItemByOrderId(Long orderId, Long itemId) {
        return itemMapper.toDto(orderItemRepository.findByOrderId(orderId, itemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get order item by id " + itemId + " and order id " + orderId)));
    }
}
