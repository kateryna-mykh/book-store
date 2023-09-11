package bookstore.mapper;

import bookstore.config.MapperConfig;
import bookstore.dto.AddCartItemToCartRequestDto;
import bookstore.dto.CartItemDto;
import bookstore.model.Book;
import bookstore.model.CartItem;
import bookstore.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(target = "id", ignore = true)
    CartItem toModel(AddCartItemToCartRequestDto requestDto, Book book, ShoppingCart shoppingCart);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemDto toDto(CartItem cartItem);
}
