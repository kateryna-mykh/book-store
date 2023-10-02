package bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bookstore.dto.AddCartItemToCartRequestDto;
import bookstore.dto.CartItemBookQty;
import bookstore.dto.CartItemDto;
import bookstore.dto.ShoppingCartDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    private static ShoppingCartDto expectedDto;
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
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("/database/users/insert-2users.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "/database/shopping-carts&items/insert-shopping-carts&items.sql"));
        }
        List<CartItemDto> cartItems = new ArrayList<>();
        cartItems.add(
                new CartItemDto().setId(1L).setBookId(1L).setBookTitle("Book 1").setQuantity(1));
        cartItems.add(
                new CartItemDto().setId(2L).setBookId(2L).setBookTitle("Book 2").setQuantity(1));
        expectedDto = new ShoppingCartDto();
        expectedDto.setId(1L);
        expectedDto.setCartItems(cartItems);
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
                    "/database/shopping-carts&items/delete-users&carts&items.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "/database/books/delete-all-books-join-to-categories.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("/database/categories/delete-all-categories.sql"));
        }
    }

    @Test
    @WithMockUser(username = "user@i.ua", authorities = { "USER" })
    @DisplayName("Get user's shopping cart, return ShoppingCartDto with 2 cartItems")
    void getUserCart_WithContext_ReturnShoppingCartDto() throws Exception {
        MvcResult result = mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/cart").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        assertEquals(2, actual.getCartItems().size());
        EqualsBuilder.reflectionEquals(expectedDto, actual);
    }

    @Test
    @Sql(scripts = "classpath:database/shopping-carts&items/delete-cartItem-with-id3.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user@i.ua", authorities = { "USER" })
    @DisplayName("""
            Add book to the shopping cart, return updated ShoppingCartDto
            with 201 HttpStatus.CREATED""")
    void addBookToCart_ValidAddCartItemToCartRequestDto_ReturnStatusCreated() throws Exception {
        AddCartItemToCartRequestDto addCratItemDto = new AddCartItemToCartRequestDto();
        ReflectionTestUtils.setField(addCratItemDto, "bookId", 3L);
        ReflectionTestUtils.setField(addCratItemDto, "quantity", 2);
        String jsonRequest = objectMapper.writeValueAsString(addCratItemDto);
        CartItemDto expected = new CartItemDto().setId(3L).setBookId(addCratItemDto.getBookId())
                .setBookTitle("Book 3").setQuantity(addCratItemDto.getQuantity());

        MvcResult result = mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        assertEquals(3, actual.getCartItems().size());
        EqualsBuilder.reflectionEquals(expected, actual.getCartItems().get(2));
    }

    @Test
    @Sql(scripts = "classpath:database/shopping-carts&items/reset-updated-cartItem.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user@i.ua", authorities = { "USER" })
    @DisplayName("Update quantity of a book in the shopping cart, return updated ShoppingCartDto")
    void updateBookQty_ValidParams_Ok() throws Exception {
        CartItemBookQty bookQty = new CartItemBookQty(2);
        String jsonRequest = objectMapper.writeValueAsString(bookQty);

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.put("/cart/cart-items/{cartItemId}", 1)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        assertEquals(2, actual.getCartItems().get(1).getQuantity());
    }

    @Test
    @Sql(scripts = "classpath:database/shopping-carts&items/insert-deleted-item.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user@i.ua", authorities = { "USER" })
    @DisplayName("Delete a book from the shopping cart, return updated ShoppingCartDto")
    void deleteBookFromCart_ValidItemId_ReturnStatusNoContent() throws Exception {
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.delete("/cart/cart-items/{cartItemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();
        
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        assertEquals(1, actual.getCartItems().size());
        assertEquals(2, actual.getCartItems().get(0).getId());
    }
}
