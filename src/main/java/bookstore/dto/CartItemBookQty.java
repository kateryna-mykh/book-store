package bookstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemBookQty(@NotNull @Positive Integer quantity) {
}
