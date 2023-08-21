package bookstore.service;

import bookstore.dto.BookDto;
import bookstore.dto.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto product);

    List<BookDto> findAll(Pageable pageable);
    
    BookDto findById(Long id);
    
    BookDto update(Long id, CreateBookRequestDto product);
    
    void deleteById(Long id);
}
