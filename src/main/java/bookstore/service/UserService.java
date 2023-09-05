package bookstore.service;

import bookstore.dto.UserRegistrationRequestDto;
import bookstore.dto.UserResponseDto;
import bookstore.exception.RegistrationException;

public interface UserService {
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException;
}
