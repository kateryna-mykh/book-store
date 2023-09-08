package bookstore.repository;

import bookstore.model.Book;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = "SELECT DISTINCT b FROM Book b INNER JOIN b.categories c "
            + "where c.id = ?1 AND c.isDeleted = false")
    List<Book> findAllByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"categories"})
    Page<Book> findAll(Pageable pageable);
}
