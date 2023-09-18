package bookstore.repository;

import bookstore.model.OrderItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query(value = "SELECT DISTINCT oi FROM OrderItem oi JOIN FETCH oi.book where oi.order.id=?1 "
            + "AND oi.isDeleted = false")
    List<OrderItem> findAllByOrderId(Long orderId);
    
    @Query(value = "SELECT oi from OrderItem oi JOIN FETCH oi.book where oi.id=?2 "
            + "AND oi.order.id=?1")
    Optional<OrderItem> findByOrderId(Long orderId, Long itemId);
}
