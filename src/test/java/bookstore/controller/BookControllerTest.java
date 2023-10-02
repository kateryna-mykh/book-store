package bookstore.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import bookstore.dto.BookDto;
import bookstore.dto.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    private static CreateBookRequestDto requestDto;
    private static BookDto expectedDto;
    private static final String DEFAULT_URL = "https://example.com/default-cover-image.jpg";   
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUpBeforeClass(@Autowired DataSource data,
            @Autowired WebApplicationContext appContext) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(appContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        tearDown(data);
        try (Connection connection = data.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("/database/categories/insert-categories.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("/database/books/insert-books-join-to-categories.sql"));
        }
        requestDto = new CreateBookRequestDto();
        ReflectionTestUtils.setField(requestDto, "title", "Book 5");
        ReflectionTestUtils.setField(requestDto, "author", "Author B");
        ReflectionTestUtils.setField(requestDto, "isbn", "978-1234567894");
        ReflectionTestUtils.setField(requestDto, "price", BigDecimal.valueOf(5.00));
        ReflectionTestUtils.setField(requestDto, "coverImage", DEFAULT_URL);
        ReflectionTestUtils.setField(requestDto, "categoryIds", List.of(1L, 2L));
        expectedDto = new BookDto().setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor()).setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice()).setCoverImage(requestDto.getCoverImage())
                .setCategoryIds(requestDto.getCategoryIds());
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

    @Test
    @Sql(scripts = "classpath:database/books/delete-book-with-978-1234567894-isbn.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("Create a new book, return BookDto with 201 HttpStatus.CREATED")
    void createBook_ValidCreateBookRequestDto_ReturnStatusCreated() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expectedDto, actual, "id");
    }
    
    @Test
    @WithMockUser(username = "user", authorities = { "USER" })
    @DisplayName("Get all available books, return List<BookDto> with 3 objects")
    void getAll_DefaultPageable_ReturnListOfCategoryDtos() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(1L).setTitle("Book 1").setAuthor("Author A")
                .setIsbn("978-1234567890").setPrice(BigDecimal.valueOf(5.00)).setDescription("")
                .setCoverImage(DEFAULT_URL).setCategoryIds(List.of(1L, 3L)));
        expected.add(new BookDto().setId(2L).setTitle("Book 2").setAuthor("Author B")
                .setIsbn("978-1234567891").setPrice(BigDecimal.valueOf(7.30)).setDescription("")
                .setCoverImage(DEFAULT_URL).setCategoryIds(List.of(2L)));
        expected.add(new BookDto().setId(3L).setTitle("Book 3").setAuthor("Author C")
                .setIsbn("978-1234567892").setPrice(BigDecimal.valueOf(11.20)).setDescription("")
                .setCoverImage(DEFAULT_URL).setCategoryIds(List.of(1L)));

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                BookDto[].class);
        assertEquals(3, actual.length);
        EqualsBuilder.reflectionEquals(expected, Arrays.stream(actual).toList());
    }
    
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DisplayName("Get available book by id, return BookDto")
    void getBookById_ValidValue_ReturnBookDto() throws Exception {
        BookDto expected = new BookDto().setId(3L).setTitle("Book 3").setAuthor("Author C")
                .setIsbn("978-1234567892").setPrice(BigDecimal.valueOf(11.20)).setDescription("")
                .setCoverImage(DEFAULT_URL).setCategoryIds(List.of(1L));

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get("/books/{id}", 3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
       
        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }
    
    @Test
    @Sql(scripts = "classpath:database/books/reset-upadated-book.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("Update a book, return updated BookDto")
    void update_ExistingId_Ok() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        expectedDto.setId(1L).setDescription("");
        
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.put("/books/{id}", 1)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        
        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expectedDto, actual);
    }
    
    @Test
    @Sql(scripts = "classpath:database/books/reset-upadated-book.sql", 
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("Verify delete() method works")
    void delete_ReturnStatusNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/books/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
