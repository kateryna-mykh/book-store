package bookstore.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderAddressDto(@NotBlank String shippingAddress) {
}
