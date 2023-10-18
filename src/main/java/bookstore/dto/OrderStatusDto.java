package bookstore.dto;

import bookstore.lib.EnumMatchString;
import bookstore.model.Order;
import jakarta.validation.constraints.NotEmpty;

public record OrderStatusDto(
        @NotEmpty @EnumMatchString(enumClass = Order.Status.class) String status) {
}
