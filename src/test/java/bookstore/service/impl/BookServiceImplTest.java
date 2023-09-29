package bookstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookstore.dto.BookDto;
import bookstore.dto.BookDtoWithoutCategoryIds;
import bookstore.dto.CreateBookRequestDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.BookMapper;
import bookstore.model.Book;
import bookstore.model.Category;
import bookstore.repository.BookRepository;
import bookstore.repository.CategoryRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    private static Long id = 1L;
    private static Book book;
    private static Category scienceFiction;
    private static CreateBookRequestDto createBookDto;
    private static BookDto bookDto;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeAll
    static void setUpBeforeClass() {
        book = new Book();
        book.setId(id);
        book.setTitle("Brave New World");
        book.setAuthor("Aldous Huxley");
        book.setIsbn("978-617-679-333-5");
        book.setPrice(BigDecimal.valueOf(11.11));
        book.setCoverImage("https://example.com/updated-cover-image.jpg");
        scienceFiction = new Category();
        scienceFiction.setId(id);
        scienceFiction.setName("Science fiction");
        book.setCategories(Set.of(scienceFiction));

        createBookDto = new CreateBookRequestDto();
        ReflectionTestUtils.setField(createBookDto, "title", book.getTitle());
        ReflectionTestUtils.setField(createBookDto, "author", book.getAuthor());
        ReflectionTestUtils.setField(createBookDto, "isbn", book.getIsbn());
        ReflectionTestUtils.setField(createBookDto, "price", book.getPrice());
        ReflectionTestUtils.setField(createBookDto, "coverImage", book.getCoverImage());
        ReflectionTestUtils.setField(createBookDto, "categoryIds", List.of(id));

        bookDto = new BookDto();
        bookDto.setId(id);
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategoryIds(List.of(id));
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidCreateBookRequestDto_ReturnBookDto() {
        when(categoryRepository.findAllById(List.of(id))).thenReturn(List.of(scienceFiction));
        when(bookMapper.toModel(createBookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        
        BookDto actual = bookService.save(createBookDto);
        
        assertThat(actual).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("Given dafault Pagable, return List<BookDto>")
    void findAll_DefaultPagable_ReturnListOfBookDtos() {
        PageRequest pageable = PageRequest.of(0, 20);
        PageImpl<Book> bookList = new PageImpl<>(List.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(bookRepository.findAll(pageable)).thenReturn(bookList);

        List<BookDto> actualList = bookService.findAll(pageable);

        assertEquals(1, actualList.size());
        assertTrue(!actualList.get(0).getCategoryIds().isEmpty());
    }

    @Test
    @DisplayName("Given valid id, retrieve the BookDto")
    void getById_WithValidId_ReturnCategoryDto() {
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        
        BookDto actual = bookService.findById(id);
        
        assertThat(actual).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("Given not existing id, retrieve the EntityNotFoundException exception")
    void getById_WithNotExistingId_ThrowEntityNotFoundException() {
        Long notExistingId = 100L;
        String expectedException = "Can't find book by id: " + notExistingId;
        when(bookRepository.findById(notExistingId)).thenReturn(Optional.empty());

        EntityNotFoundException actualException = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(notExistingId));

        assertEquals(expectedException, actualException.getMessage());
    }

    @Test
    @DisplayName("Verify delete() method works")
    void deleteById_AnyId_CallOneTime() {
        doNothing().when(bookRepository).deleteById(anyLong());
        bookService.deleteById(anyLong());
        verify(bookRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Verify update() method works")
    void update_ValidParams_ReturnBookDto() {
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(categoryRepository.findAllById(List.of(id))).thenReturn(List.of(scienceFiction));
        when(bookMapper.toModel(createBookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);        
        
        BookDto actual = bookService.update(id, createBookDto);
        
        assertThat(actual.getId()).isEqualTo(id);
        EqualsBuilder.reflectionEquals(createBookDto, actual, "id", "categoryIds");
    }
    
    @Test
    @DisplayName("""            
            Verify update(), given not existing id, retrieve the 
            EntityNotFoundException exception""")
    void update_WithNotExistingId_ThrowEntityNotFoundException() {
        Long notExistingId = 100L;
        String expectedException = "Can't find book by id: " + notExistingId;
        when(bookRepository.findById(notExistingId)).thenReturn(Optional.empty());

        EntityNotFoundException actualException = assertThrows(EntityNotFoundException.class,
                () -> bookService.update(notExistingId, createBookDto));

        assertEquals(expectedException, actualException.getMessage());
    }
    
    @Test
    @DisplayName("Given category id and dafault Pagable, return List<BookDtoWithoutCategoryIds>")
    void findAllBooksByCategoryId_ValidParams_ReturnBookDto() {
        BookDtoWithoutCategoryIds returnDto = new BookDtoWithoutCategoryIds();
        returnDto.setId(id);
        returnDto.setTitle(book.getTitle());
        returnDto.setAuthor(book.getAuthor());
        returnDto.setIsbn(book.getIsbn());
        returnDto.setPrice(book.getPrice());
        returnDto.setCoverImage(book.getCoverImage());
        PageRequest pageable = PageRequest.of(0, 20);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(returnDto);
        when(bookRepository.findAllByCategoryId(id, pageable)).thenReturn(List.of(book));

        List<BookDtoWithoutCategoryIds> actualList = bookService.findAllBooksByCategoryId(id,
                pageable);

        assertEquals(1, actualList.size());
    }
}
