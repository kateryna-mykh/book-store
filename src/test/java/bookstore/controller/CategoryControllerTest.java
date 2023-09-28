package bookstore.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import bookstore.dto.BookDtoWithoutCategoryIds;
import bookstore.dto.CategoryDto;
import bookstore.dto.CreateCategoryDto;
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
class CategoryControllerTest {
    protected static MockMvc mockMvc;
    private static CreateCategoryDto requestDto;
    private static CategoryDto expectedDto;
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
        }
        requestDto = new CreateCategoryDto();
        ReflectionTestUtils.setField(requestDto, "name", "Non-Fiction");
        ReflectionTestUtils.setField(requestDto, "description", "true based stories");
        expectedDto = new CategoryDto()
                .setId(1L)
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());
    }

    @AfterAll
    static void tearDownAfterClass(@Autowired DataSource data) throws Exception {
        tearDown(data);
    }

    @SneakyThrows
    static void tearDown(DataSource data) {
        try (Connection connection = data.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("/database/categories/delete-all-categories.sql"));
        }
    }

    @Test
    @Sql(scripts = "classpath:database/categories/delete-non-fiction-category.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("Create a new category, return CategoryDto with 201 HttpStatus.CREATED")
    void createCategory_ValidCreateCategoryDto_ReturnStatusCreated() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expectedDto, actual, "id");
    }

    @Test
    @WithMockUser(username = "user", authorities = { "USER" })
    @DisplayName("Get all active categories, return List<CategoryDto> with 2 objects")
    void getAll_DefaultPageable_ReturnListOfCategoryDtos() throws Exception {
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(new CategoryDto().setId(1L).setName("Fiction")
                .setDescription("Literature created from the imagination"));
        expected.add(new CategoryDto().setId(2L).setName("Drama")
                .setDescription("The specific mode of fiction represented in performance"));

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CategoryDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                CategoryDto[].class);
        assertEquals(2, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }
    
    @Test
    @Sql(scripts = "classpath:/database/categories/reset-category-byID-1.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("Update a category, return updated CategoryDto")
    void updateCategory_ExistingId_Ok() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.put("/categories/{id}", 1)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        
        CategoryDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertEquals(expectedDto, actual);
    }
    
    @Test
    @Sql(scripts = "classpath:/database/categories/reset-category-byID-1.sql", 
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DisplayName("Verify delete() method works")
    void deleteCategory_ReturnStatusNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DisplayName("Get existing category by id, return CategoryDto")
    void getCategoryById_ValidValue_ReturnCategoryDto() throws Exception {
        CategoryDto expected = new CategoryDto()
                .setId(2L)
                .setName("Drama")
                .setDescription("The specific mode of fiction represented in performance");

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get("/categories/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
       
        CategoryDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
    
    @Test
    @Sql(scripts = "classpath:/database/books/insert-books-join-to-categories.sql", 
            executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:/database/books/delete-all-books-join-to-categories.sql", 
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", authorities = { "USER" })
    @DisplayName("Get books by category id, return List<BookDtoWithoutCategoryIds> with 1 object")
    void getBooksByCategoryId_ValidValue_ReturnListOfBookDtoWithoutCategoryIds() throws Exception {
        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        expected.add(new BookDtoWithoutCategoryIds().setId(2L).setTitle("Book 2")
                .setAuthor("Author B").setIsbn("978-1234567891").setPrice(BigDecimal.valueOf(7.30))
                .setDescription("")
                .setCoverImage("https://example.com/default-cover-image.jpg"));

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get("/categories/{id}/books", 2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        
        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class);
        assertEquals(1, actual.length);
        EqualsBuilder.reflectionEquals(expected, Arrays.stream(actual).toList());
    }
}
