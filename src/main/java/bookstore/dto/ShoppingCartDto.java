package bookstore.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingCartDto {
    private Long id;
    private List<CartItemDto> cartItems;
}
