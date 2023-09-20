package bookstore.service.impl;

import bookstore.dto.OrderAddressDto;
import bookstore.dto.OrderDto;
import bookstore.dto.OrderItemDto;
import bookstore.dto.OrderStatusDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.OrderMapper;
import bookstore.model.Order;
import bookstore.model.OrderItem;
import bookstore.model.ShoppingCart;
import bookstore.repository.OrderRepository;
import bookstore.service.OrderItemService;
import bookstore.service.OrderService;
import bookstore.service.ShoppingCartService;
import bookstore.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserService userService;
    private final ShoppingCartService shoppingCartService;
    private final OrderItemService orderItemService;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper,
            UserService userService, ShoppingCartService shoppingCartService, 
            OrderItemService orderItemService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.userService = userService;
        this.shoppingCartService = shoppingCartService;
        this.orderItemService = orderItemService;
    }

    @Override
    public List<OrderDto> getAll(Pageable pageable) {
        return orderRepository.findAllByUserId(userService.getUser().getId(), pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto save(OrderAddressDto shippingAddress) {
        ShoppingCart shoppingCart = userService.getUserCart();
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new RuntimeException("Can't create an empty order");
        }
        Order newOrder = new Order();
        newOrder.setUser(shoppingCart.getUser());
        newOrder.setShippingAddress(shippingAddress.shippingAddress());
        if (shoppingCart.getUser().getShippingAddress() == null) {
            userService.updateShippingAddress(shippingAddress);
        }
        newOrder.setOrderDate(LocalDateTime.now());
        Order preSavedOrder = orderRepository.save(newOrder);
        
        Set<OrderItem> orderItems = orderItemService.saveAll(shoppingCart, preSavedOrder);
        preSavedOrder.getOrderItems().addAll(orderItems);
        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        preSavedOrder.setTotal(totalPrice);
        shoppingCartService.clear();
        return orderMapper.toDto(orderRepository.save(preSavedOrder));
    }

    @Override
    public OrderDto updateStatus(Long orderId, OrderStatusDto status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Can't get order by id " + orderId));
        order.setStatus(Order.Status.valueOf(status.status()));
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderItemDto> getOrderItems(Long orderId) {
        if (!isUserOrderOwner(orderId)) {
            throw new RuntimeException("This user doesn't have order with id " + orderId);
        }
        return orderItemService.getItemsByOrderId(orderId);
    }

    @Override
    public OrderItemDto getOrderItem(Long orderId, Long itemId) {
        if (!isUserOrderOwner(orderId)) {
            throw new RuntimeException("This user doesn't have order with id " + orderId);
        }
        return orderItemService.getItemByOrderId(orderId, itemId);
    }
    
    private boolean isUserOrderOwner(Long orderId) {
        Long userId = userService.getUser().getId();
        Long orderOwnerId = orderRepository.getReferenceById(orderId).getUser().getId();
        return userId.equals(orderOwnerId);
    }
}
