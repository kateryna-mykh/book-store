package bookstore.dto;

import jakarta.validation.constraints.Positive;

public record CartItemBookQty(@Positive Integer quantity) {
}
