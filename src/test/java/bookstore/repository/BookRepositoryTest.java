package bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bookstore.model.Book;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    static void setUpBeforeClass(@Autowired DataSource data) throws SQLException {
        tearDown(data);
        try (Connection connection = data.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("/database/categories/insert-categories.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("/database/books/insert-books-join-to-categories.sql"));
        }
    }
    
    @Test
    @DisplayName("Find all books by available category id and dafault Pagable, return List<Book>")
    void findAllByCategoryId_ValidParams_ReturnTwoBooks() {
        List<Book> actualList = bookRepository.findAllByCategoryId(1L, PageRequest.of(0, 20));
        assertEquals(2, actualList.size());
        assertEquals(1L, actualList.get(0).getId());
        assertEquals(3L, actualList.get(1).getId());
    }
    
    @Test
    @DisplayName("""
           Find all books by deleted category and dafault Pagable, return empty List<Book>""")
    void findAllByCategoryId_ValidParams_ReturnEmptyList() {
        List<Book> actualList = bookRepository.findAllByCategoryId(3L, PageRequest.of(0, 20));
        assertEquals(0, actualList.size());
    }
    
    @AfterAll
    static void tearDownAfterClass(@Autowired DataSource data) throws Exception {
        tearDown(data);
    }

    @SneakyThrows
    static void tearDown(DataSource data) {
        try (Connection connection = data.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "/database/books/delete-all-books-join-to-categories.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("/database/categories/delete-all-categories.sql"));
        }
    }
}
