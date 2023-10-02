package bookstore.repository;

import static org.junit.Assert.assertEquals;

import bookstore.model.CartItem;
import java.util.Set;
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
class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Delete all cart items from shopping cart by cart id, verify items deleted")
    void deleteAllByShoppingCartId_CartId_Ok() {
        cartItemRepository.deleteAllByShoppingCartId(1L);
        Set<CartItem> itemsAfter = shoppingCartRepository.findById(1L).get().getCartItems();
        assertEquals(0, itemsAfter.size());
    }
}
