package bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class UserLoginRequestDto {
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    private String password;
}
