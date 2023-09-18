package bookstore.service;

import bookstore.dto.OrderAddressDto;
import bookstore.dto.UserRegistrationRequestDto;
import bookstore.dto.UserResponseDto;
import bookstore.exception.RegistrationException;
import bookstore.model.ShoppingCart;
import bookstore.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;

    ShoppingCart getUserCart();
    
    User getUser();
    
    void updateShippingAddress(OrderAddressDto shippingAddress);
}
