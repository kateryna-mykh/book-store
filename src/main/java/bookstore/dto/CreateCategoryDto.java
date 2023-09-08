package bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateCategoryDto {
    @NotBlank
    @Size(min = 2, max = 50)
    private String name;
    private String description;
}
