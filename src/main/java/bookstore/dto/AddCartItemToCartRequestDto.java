package bookstore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
public class AddCartItemToCartRequestDto {
    @NotNull
    private Long bookId;
    @Range(min = 1, max = Integer.MAX_VALUE)
    private Integer quantity;
}
