package bookstore.service;

import bookstore.dto.UserRegistrationRequestDto;
import bookstore.dto.UserResponseDto;
import bookstore.exception.RegistrationException;
import bookstore.model.ShoppingCart;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;

    ShoppingCart getUserCart();
}
