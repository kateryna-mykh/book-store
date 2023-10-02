package bookstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookstore.dto.AddCartItemToCartRequestDto;
import bookstore.dto.CartItemBookQty;
import bookstore.dto.ShoppingCartDto;
import bookstore.mapper.ShoppingCartMapper;
import bookstore.model.Book;
import bookstore.model.CartItem;
import bookstore.model.ShoppingCart;
import bookstore.repository.ShoppingCartRepository;
import bookstore.service.CartItemService;
import bookstore.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    private static Long id = 1L;
    private static CartItem cartItem;
    private static Book book;
    private static ShoppingCart shoppingCart;
    private static AddCartItemToCartRequestDto cartItemDto;
    private static ShoppingCartDto returnCartDto;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemService cartItemService;
    @Mock
    private UserService userService;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        book = new Book();
        book.setId(id);
        shoppingCart = new ShoppingCart();
        shoppingCart.setId(id);
        cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(1);
        cartItem.setShoppingCart(shoppingCart);
        
        returnCartDto = new ShoppingCartDto();
        returnCartDto.setId(shoppingCart.getId());
        returnCartDto.setCartItems(null);
    }

    @Test
    @DisplayName("Verify getShoppingCart() method works")
    void getShoppingCart_CurrentUserCart_ReturnShoppingCartDto() {
        when(userService.getUserCart()).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(returnCartDto);
        ShoppingCartDto actualCart = shoppingCartService.getShoppingCart();
        assertThat(actualCart).isEqualTo(returnCartDto);
    }
    
    @Test
    @DisplayName("Verify delete() method works")
    void deleteById_AnyId_CallOneTime() {
        doNothing().when(shoppingCartRepository).deleteById(anyLong());
        shoppingCartService.deleteById(anyLong());
        verify(shoppingCartRepository, times(1)).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("Verify addBookToCart() method works, return updated ShoppingCartDto")
    void addBookToCart_ValidAddCartItemToCartRequestDto_ReturnShoppingCartDto() {
        when(cartItemService.save(cartItemDto)).thenReturn(cartItem);
        when(userService.getUserCart()).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(returnCartDto);
        shoppingCart.getCartItems().add(cartItem);
        
        ShoppingCartDto actualCart = shoppingCartService.addBookToCart(cartItemDto);
        
        assertThat(actualCart).isEqualTo(returnCartDto);
    }
    
    @Test
    @DisplayName("Verify updateBookQty() method works, return updated ShoppingCartDto")
    void updateBookQty_ValidParams_ReturnShoppingCartDto() {    
        when(cartItemService.getById(id)).thenReturn(cartItem);
        when(cartItemService.update(cartItem)).thenReturn(cartItem);
        when(userService.getUserCart()).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(returnCartDto);
        
        ShoppingCartDto actualCart = shoppingCartService.updateBookQty(id, new CartItemBookQty(2));
        
        assertNotNull(actualCart);
        assertEquals(returnCartDto, actualCart);
    }
    
    @Test
    @DisplayName("Verify deleteBookFromCart() method works, return updated ShoppingCartDto")
    void deleteBookFromCart_AnyId_ReturnShoppingCartDto() {
        doNothing().when(cartItemService).delete(id);
        shoppingCartService.deleteBookFromCart(id);
        verify(cartItemService, times(1)).delete(anyLong());
    }
    
    @Test
    @DisplayName("Verify clear() method works")
    void clear_thisCart_CallOneTime() {
        when(userService.getUserCart()).thenReturn(shoppingCart);
        doNothing().when(cartItemService).deleteAllByShoppingCartId(id);
        shoppingCartService.clear();
        verify(cartItemService, times(1)).deleteAllByShoppingCartId(id);
    }
}
