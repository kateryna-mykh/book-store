package bookstore.service;

import bookstore.dto.AddCartItemToCartRequestDto;
import bookstore.dto.CartItemBookQty;
import bookstore.dto.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart();

    ShoppingCartDto addBookToCart(AddCartItemToCartRequestDto cartItemDto);

    ShoppingCartDto updateBookQty(Long cartItemId, CartItemBookQty qty);

    ShoppingCartDto deleteBookFromCart(Long cartItemId);

    void deleteById(Long id);
    
    void clear();
}
