package bookstore.service;

import bookstore.dto.AddCartItemToCartRequestDto;
import bookstore.model.CartItem;

public interface CartItemService {
    CartItem save(AddCartItemToCartRequestDto requestDto);

    CartItem getById(Long cartItemId);

    void delete(Long cartItemId);
    
    CartItem update(CartItem cartItem);
}
