package bookstore.repository;

import bookstore.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Override
    @Query(value = "SELECT DISTINCT sc FROM ShoppingCart sc LEFT JOIN FETCH sc.cartItems ci "
            + "LEFT JOIN FETCH ci.book where ci.shoppingCart.id = ?1")
    Optional<ShoppingCart> findById(Long id);
}
