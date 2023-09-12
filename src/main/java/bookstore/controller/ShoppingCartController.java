package bookstore.controller;

import bookstore.dto.AddCartItemToCartRequestDto;
import bookstore.dto.CartItemBookQty;
import bookstore.dto.ShoppingCartDto;
import bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping Cart management", description = "Endpoints for managing user's shopping cart")
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService cartService) {
        this.shoppingCartService = cartService;
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(summary = "Get user's shopping cart", description = "Get user's shopping cart")
    public ShoppingCartDto getUserCart() {
        return shoppingCartService.getShoppingCart();
    }

    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Add book to the shopping cart", 
            description = "Add book by id and it's qty to the shopping cart")
    public ShoppingCartDto addBookToCart(
            @RequestBody @Valid AddCartItemToCartRequestDto cartItemDto) {
        return shoppingCartService.addBookToCart(cartItemDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Update quantity of a book in the shopping cart", 
            description = "Update quantity of a book in the shopping cart")
    public ShoppingCartDto updateBookQty(@PathVariable Long cartItemId,
            @RequestBody @Valid CartItemBookQty quantity) {
        return shoppingCartService.updateBookQty(cartItemId, quantity);
    }

    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Delete a book from the shopping cart", 
            description = "Delete a book from the shopping cart")
    public ShoppingCartDto deleteBookFromCart(@PathVariable Long cartItemId) {
        return shoppingCartService.deleteBookFromCart(cartItemId);
    }
}
