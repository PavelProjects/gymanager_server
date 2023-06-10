package ru.pobopo.services.gateway.objects;

import java.util.List;

public class UserInfo {
    private String userLogin;
    private String userId;
    private List<String> roles;

    public UserInfo() {
    }

    public UserInfo(String userLogin, String userId, List<String> roles) {
        this.userLogin = userLogin;
        this.userId = userId;
        this.roles = roles;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
