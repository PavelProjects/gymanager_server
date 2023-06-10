package ru.pobopo.gymanager.services.user.service.services;

import java.util.Objects;
import javax.naming.AuthenticationException;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.pobopo.gymanager.services.user.service.entity.UserEntity;

public class PermitService {
    public static final String ADMIN_ROLE = "admin";

    public static boolean canEditUser(UserEntity entity) throws AuthenticationException {
        Objects.requireNonNull(entity);
        Authentication authentication = getCurrentUser();
        return isSameUser(authentication, entity.getLogin()) || isAdmin(authentication);
    }

    public static String getCurrentUserName() throws AuthenticationException {
        return getCurrentUser().getName();
    }

    private static boolean isSameUser(Authentication authentication, String login) {
        return StringUtils.equals(authentication.getName(), login);
    }

    private static boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority -> StringUtils.equals(grantedAuthority.getAuthority(), ADMIN_ROLE));
    }

    @NonNull
    private static Authentication getCurrentUser() throws AuthenticationException {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null || StringUtils.isBlank(currentUser.getName())) {
            throw new AuthenticationException("Current user didn't authenticate!");
        }
        return currentUser;
    }
}
