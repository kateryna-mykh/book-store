package bookstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookstore.dto.AddCartItemToCartRequestDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.CartItemMapper;
import bookstore.model.Book;
import bookstore.model.CartItem;
import bookstore.model.ShoppingCart;
import bookstore.repository.BookRepository;
import bookstore.repository.CartItemRepository;
import bookstore.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CartItemServiceImplTest {
    private static Long id = 1L;
    private static CartItem cartItem;
    private static Book book;
    private static ShoppingCart shoppingCart;
    private static AddCartItemToCartRequestDto cartItemDto;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private CartItemServiceImpl cartItemService;

    @BeforeAll
    static void setUpBeforeClass() {
        book = new Book();
        book.setId(id);
        shoppingCart = new ShoppingCart();
       
        cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(1);
        cartItem.setShoppingCart(shoppingCart);
        
        cartItemDto = new AddCartItemToCartRequestDto();
        ReflectionTestUtils.setField(cartItemDto, "bookId", cartItem.getBook().getId());
        ReflectionTestUtils.setField(cartItemDto, "quantity", cartItem.getQuantity());
    }

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidAddCartItemToCartRequestDto_ReturnCartItem() {
        shoppingCart.getCartItems().clear();
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(userService.getUserCart()).thenReturn(shoppingCart);
        when(cartItemMapper.toModel(cartItemDto, book, shoppingCart)).thenReturn(cartItem);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        
        CartItem actual = cartItemService.save(cartItemDto);
        
        assertThat(actual).isEqualTo(cartItem);
    }

    @Test
    @DisplayName("Verify save(), given not active book id, retrieve the EntityNotFoundException")
    void save_InvalidBookId_ThrowEntityNotFoundException() {
        String expectedException = "Can't find book by id: " + id;
        when(bookRepository.findById(cartItemDto.getBookId())).thenReturn(Optional.empty());

        EntityNotFoundException actualException = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.save(cartItemDto));

        assertEquals(expectedException, actualException.getMessage());       
    }

    @Test
    @DisplayName("""
            Verify save(), given cart item that already added to shopping cart,
            retrieve the RuntimeException""")
    void save_addSameBookItem_ThrowRuntimeException() {
        shoppingCart.getCartItems().add(cartItem);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(userService.getUserCart()).thenReturn(shoppingCart);
        when(cartItemMapper.toModel(cartItemDto, book, shoppingCart)).thenReturn(cartItem);        
        String expectedException = "Current book has already added";
        
        RuntimeException actualException = assertThrows(RuntimeException.class,
                () -> cartItemService.save(cartItemDto));

        assertEquals(expectedException, actualException.getMessage());
    }

    @Test
    @DisplayName("Given valid id, retrieve the CartItem")
    void getById_WithValidId_ReturnCartItem() {
        when(cartItemRepository.findById(id)).thenReturn(Optional.of(cartItem));       
        CartItem actual = cartItemService.getById(id);        
        assertThat(actual).isEqualTo(cartItem);
    }

    @Test
    @DisplayName("Given not existing id, retrieve the EntityNotFoundException exception")
    void getById_WithNotExistingId_ThrowEntityNotFoundException() {
        Long notExistingId = 100L;
        String expectedException = "Can't get cart item by id " + notExistingId;
        when(cartItemRepository.findById(notExistingId)).thenReturn(Optional.empty());

        EntityNotFoundException actualException = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.getById(notExistingId));

        assertEquals(expectedException, actualException.getMessage());
    }

    @Test
    @DisplayName("Verify update() method works")
    void update_ValidParam_CartItem() {
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem); 
        CartItem actual = cartItemService.update(cartItem);        
        assertNotNull(actual);
        assertEquals(cartItem, actual);
    }

    @Test
    @DisplayName("Verify delete() method works")
    void delete_AnyId_CallOneTime() {
        doNothing().when(cartItemRepository).deleteById(anyLong());
        cartItemService.delete(anyLong());
        verify(cartItemRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Verify deleteAllByShoppingCartId() method works")
    void deleteAllByShoppingCartId_AnyId_CallOneTime() {
        doNothing().when(cartItemRepository).deleteAllByShoppingCartId(anyLong());
        cartItemService.deleteAllByShoppingCartId(anyLong());
        verify(cartItemRepository, times(1)).deleteAllByShoppingCartId(anyLong());
    }
}
