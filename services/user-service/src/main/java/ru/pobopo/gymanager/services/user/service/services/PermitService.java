package ru.pobopo.gymanager.services.user.service.services;

import java.util.Objects;
import javax.naming.AuthenticationException;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import ru.pobopo.gymanager.services.user.service.context.RequestContextHolder;
import ru.pobopo.gymanager.services.user.service.entity.UserEntity;
import ru.pobopo.gymanager.shared.objects.AuthorizedUserInfo;

public class PermitService {
    public static final String ADMIN_ROLE = "admin";

    public static boolean canEditUser(UserEntity entity) throws AuthenticationException {
        Objects.requireNonNull(entity);
        AuthorizedUserInfo authentication = getCurrentUser();
        return isSameUser(authentication, entity) || isAdmin(authentication);
    }

    public static String getCurrentUserName() throws AuthenticationException {
        return getCurrentUser().getUserLogin();
    }

    private static boolean isSameUser(AuthorizedUserInfo authentication, UserEntity user) {
        return StringUtils.equals(authentication.getUserLogin(), user.getLogin())
               && StringUtils.equals(authentication.getUserId(), user.getId());
    }

    private static boolean isAdmin(AuthorizedUserInfo authentication) {
        if (authentication.getRolesList() == null || authentication.getRolesList().isEmpty()) {
            return false;
        }
        return authentication.getRolesList().stream().anyMatch(role -> StringUtils.equals(role, ADMIN_ROLE));
    }

    @NonNull
    private static AuthorizedUserInfo getCurrentUser() throws AuthenticationException {
        AuthorizedUserInfo currentUser = RequestContextHolder.getCurrentUserInfo();
        if (currentUser == null || StringUtils.isBlank(currentUser.getUserLogin())) {
            throw new AuthenticationException("Current user didn't authenticate!");
        }
        return currentUser;
    }
}
