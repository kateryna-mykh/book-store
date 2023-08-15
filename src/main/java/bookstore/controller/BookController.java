package bookstore.controller;

import bookstore.dto.BookDto;
import bookstore.dto.CreateBookRequestDto;
import bookstore.service.BookService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDto> getAll() {
        return bookService.findAll();
    }
  
    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }
  
    @PostMapping
    public BookDto createBook(@RequestBody CreateBookRequestDto book) {
        return bookService.save(book);
    }

    @PutMapping("/{id}")
    public BookDto update(@PathVariable Long id, @RequestBody CreateBookRequestDto book) {
        return bookService.update(id, book);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
