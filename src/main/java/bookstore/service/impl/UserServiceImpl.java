package bookstore.service.impl;

import bookstore.dto.OrderAddressDto;
import bookstore.dto.UserRegistrationRequestDto;
import bookstore.dto.UserResponseDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.exception.RegistrationException;
import bookstore.mapper.UserMapper;
import bookstore.model.Role;
import bookstore.model.Role.RoleName;
import bookstore.model.ShoppingCart;
import bookstore.model.User;
import bookstore.repository.ShoppingCartRepository;
import bookstore.repository.UserRepository;
import bookstore.service.RoleService;
import bookstore.service.UserService;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ShoppingCartRepository shoppingCartRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    @Value("${credential.admin}")
    private String adminEmail;
    private Authentication authentication;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            UserMapper userMapper, RoleService roleService,
            ShoppingCartRepository shoppingCartRepository) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    @Transactional(rollbackFor = RegistrationException.class)
    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration");
        }
        User user = userMapper.toModel(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<Role> userRoles = new HashSet<>();
        userRoles.add(roleService.getRoleByRoleName(RoleName.USER));
        if (user.getEmail().equals(adminEmail)) {
            userRoles.add(roleService.getRoleByRoleName(RoleName.ADMIN));
        }
        user.setRoles(userRoles);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public ShoppingCart getUserCart() {
        Long userId = getUser().getId();
        return shoppingCartRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find shopping cart by id: " + userId));
    }
    
    @Override
    public User getUser() {
        authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find user by email: " + authentication.getName()));
    }
    
    @Override
    public void updateShippingAddress(OrderAddressDto shippingAddress) {
        User user = getUser();
        user.setShippingAddress(shippingAddress.shippingAddress());
        userRepository.save(user);
    }
}
