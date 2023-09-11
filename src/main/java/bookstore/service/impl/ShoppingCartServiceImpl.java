package bookstore.service.impl;

import bookstore.dto.AddCartItemToCartRequestDto;
import bookstore.dto.CartItemBookQty;
import bookstore.dto.ShoppingCartDto;
import bookstore.mapper.ShoppingCartMapper;
import bookstore.model.CartItem;
import bookstore.repository.ShoppingCartRepository;
import bookstore.service.CartItemService;
import bookstore.service.ShoppingCartService;
import bookstore.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemService cartItemService;
    private final UserService userService;

    public ShoppingCartServiceImpl(ShoppingCartRepository cartRepository,
            ShoppingCartMapper cartMapper, CartItemService cartItemService,
            UserService userService) {
        this.shoppingCartRepository = cartRepository;
        this.shoppingCartMapper = cartMapper;
        this.cartItemService = cartItemService;
        this.userService = userService;
    }

    @Override
    public ShoppingCartDto getShoppingCart() {
        return shoppingCartMapper.toDto(userService.getUserCart());
    }

    @Override
    public void deleteById(Long id) {
        shoppingCartRepository.deleteById(id);
    }

    @Override
    public ShoppingCartDto addBookToCart(AddCartItemToCartRequestDto cartItemDto) {
        CartItem cartItem = cartItemService.save(cartItemDto);
        userService.getUserCart().getCartItems().add(cartItem);
        return getShoppingCart();
    }

    @Override
    public ShoppingCartDto updateBookQty(Long cartItemId, CartItemBookQty qty) {
        CartItem cartItem = cartItemService.getById(cartItemId);
        cartItem.setQuantity(qty.quantity());
        cartItemService.update(cartItem);
        return getShoppingCart();
    }

    @Override
    public ShoppingCartDto deleteBookFromCart(Long cartItemId) {
        cartItemService.delete(cartItemId);
        return getShoppingCart();
    }
}
