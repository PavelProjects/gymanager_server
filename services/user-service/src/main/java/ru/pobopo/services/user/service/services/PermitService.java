package ru.pobopo.services.user.service.services;

import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import ru.pobopo.services.user.service.entity.Role;
import ru.pobopo.services.user.service.entity.UserEntity;
import ru.pobopo.services.user.service.exceptions.AccessDeniedException;

public class PermitService {
    public static final String ADMIN_ROLE = "admin";

    public static boolean canEditUser(UserEntity entityToCheck, UserEntity entity) {
        Objects.requireNonNull(entityToCheck);
        Objects.requireNonNull(entity);

        if (StringUtils.equals(entityToCheck.getId(), entity.getId()) ||
            StringUtils.equals(entityToCheck.getLogin(), entity.getLogin())) {
            return true;
        }

        if (entityToCheck.getRoles() != null && !entityToCheck.getRoles().isEmpty()) {
            Optional<Role> adminRole = entityToCheck.getRoles().stream()
                .filter(role -> StringUtils.equals(role.getName(), ADMIN_ROLE)).findFirst();
            return adminRole.isPresent();
        }

        return false;
    }
}
