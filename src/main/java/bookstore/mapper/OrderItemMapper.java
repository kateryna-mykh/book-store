package bookstore.mapper;

import bookstore.config.MapperConfig;
import bookstore.dto.OrderItemDto;
import bookstore.model.CartItem;
import bookstore.model.Order;
import bookstore.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "price", source = "cartItem.book.price")
    OrderItem toModel(CartItem cartItem, Order order);
    
    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toDto(OrderItem orderItem);
}
