package bookstore.repository;

import bookstore.model.Order;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT DISTINCT o FROM Order o JOIN FETCH o.orderItems oi JOIN FETCH oi.book "
            + "where o.user.id = ?1 AND o.isDeleted = false")
    List<Order> findAllByUserId(Long userId, Pageable pageable);
}
