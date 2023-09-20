package bookstore.repository;

import bookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM CartItem ci where ci.shoppingCart.id = ?1")
    void deleteAllByShoppingCartId(Long cartId);
}
