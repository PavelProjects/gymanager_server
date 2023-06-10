package ru.pobopo.gymanager.shared.objects;


public class UserDetailsResponse {
    private String userLogin;
    private String userId;
    private String roles;

    public UserDetailsResponse() {
    }

    public UserDetailsResponse(String userLogin, String userId, String roles) {
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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
