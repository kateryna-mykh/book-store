package bookstore.service;

import bookstore.model.Role;

public interface RoleService {
    Role getRoleByRoleName(Role.RoleName roleName);
}
