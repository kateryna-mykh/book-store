package bookstore.service;

import bookstore.model.Role;
import bookstore.model.Role.RoleName;
import bookstore.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getRoleByRoleName(RoleName roleName) {
        return roleRepository.findRoleByName(roleName)
                .orElseThrow(() -> new RuntimeException("can't find role by name: " + roleName));
    }
}
