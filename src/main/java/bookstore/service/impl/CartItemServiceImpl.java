package bookstore.service.impl;

import bookstore.dto.AddCartItemToCartRequestDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.CartItemMapper;
import bookstore.model.Book;
import bookstore.model.CartItem;
import bookstore.model.ShoppingCart;
import bookstore.repository.BookRepository;
import bookstore.repository.CartItemRepository;
import bookstore.repository.ShoppingCartRepository;
import bookstore.service.CartItemService;
import bookstore.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final BookRepository bookRepository;
    private final UserService userService;

    public CartItemServiceImpl(CartItemRepository cartItemRepository, CartItemMapper cartItemMapper,
            BookRepository bookRepository, ShoppingCartRepository shoppingCartRepository,
            UserService userService) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    @Override
    public CartItem save(AddCartItemToCartRequestDto cartItemDto) {
        Book book = bookRepository.findById(cartItemDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find book by id: " + cartItemDto.getBookId()));
        ShoppingCart shoppingCart = userService.getUserCart();
        CartItem cartItem = cartItemMapper.toModel(cartItemDto, book, shoppingCart);
        if (shoppingCart.getCartItems().contains(cartItem)) {
            throw new RuntimeException("Current book has already added");
        }
        return cartItemRepository.save(cartItem);
    }

    @Override
    public CartItem getById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Can't get cart item by id " + cartItemId));
    }

    @Override
    public void delete(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public CartItem update(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }
    
    @Override
    public void deleteAllByShoppingCartId(Long cartId) {
        cartItemRepository.deleteAllByShoppingCartId(cartId);
    }
}
