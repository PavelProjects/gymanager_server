package ru.pobopo.gymanager.shared.objects;

import java.util.List;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/*
    поля final и только геттеры что бы предотвратить изменение данных авторизованного пользователя в коде
 */

@ToString
public class AuthorizedUserInfo {
    private final String userId;
    private final String userLogin;
    private final String roles;
    private final List<String> rolesList;

    public AuthorizedUserInfo(String userId, String userLogin, String roles) {
        this.userId = userId;
        this.userLogin = userLogin;
        this.roles = roles;
        if (StringUtils.isNotBlank(roles)) {
            this.rolesList = List.of(roles.split(";"));
        } else {
            this.rolesList = List.of();
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getRoles() {
        return roles;
    }

    public List<String> getRolesList() {
        return rolesList;
    }
}
