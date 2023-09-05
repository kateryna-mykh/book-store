package bookstore.service;

import bookstore.dto.UserRegistrationRequestDto;
import bookstore.dto.UserResponseDto;
import bookstore.exception.RegistrationException;
import bookstore.mapper.UserMapper;
import bookstore.model.Role;
import bookstore.model.Role.RoleName;
import bookstore.model.User;
import bookstore.repository.UserRepository;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    @Value("${credential.admin}")
    private String adminEmail;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            UserMapper userMapper, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

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
        return userMapper.toDto(userRepository.save(user));
    }
}
