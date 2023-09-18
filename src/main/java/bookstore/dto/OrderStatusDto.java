package bookstore.dto;

import bookstore.model.Order;
import jakarta.validation.constraints.NotEmpty;
import lib.EnumMatchString;

public record OrderStatusDto(
        @NotEmpty @EnumMatchString(enumClass = Order.Status.class) String status) {
}
