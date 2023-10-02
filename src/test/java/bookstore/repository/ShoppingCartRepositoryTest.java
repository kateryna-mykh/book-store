package bookstore.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import bookstore.model.ShoppingCart;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DataJpaTest
@Sql(scripts = {"classpath:/database/categories/insert-categories.sql",
        "classpath:/database/books/insert-books-join-to-categories.sql",
        "classpath:/database/users/insert-2users.sql",
        "classpath:/database/shopping-carts&items/insert-shopping-carts&items.sql"}, 
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:/database/shopping-carts&items/delete-users&carts&items.sql",
        "classpath:/database/books/delete-all-books-join-to-categories.sql",
        "classpath:/database/categories/delete-all-categories.sql"}, 
        executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Find user's shopping cart by user id, return full object Optional<ShoppingCart>")
    void findById_UserId_ReturnUserShoppingCart() {
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository.findById(1L);
        assertNotNull(shoppingCart);
        assertEquals(1L, shoppingCart.get().getId());
        assertEquals(1L, shoppingCart.get().getUser().getId());
        assertEquals(2, shoppingCart.get().getCartItems().size());
    }
    
    @Test
    @DisplayName("Find user's shopping cart by user id, return empty Optional<ShoppingCart>")
    void findById_UserId_ReturnEmptyUserShoppingCart() {
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository.findById(2L);
        assertNotNull(shoppingCart);
        assertEquals(2L, shoppingCart.get().getId());
        assertEquals(2L, shoppingCart.get().getUser().getId());
        assertTrue(shoppingCart.get().getCartItems().isEmpty());
    }
}
