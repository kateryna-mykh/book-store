package bookstore.mapper;

import bookstore.config.MapperConfig;
import bookstore.dto.ShoppingCartDto;
import bookstore.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(target = "cartItems", source = "cartItems")
    @Mapping(target = "id", source = "user.id")
    ShoppingCartDto toDto(ShoppingCart cart);
}
