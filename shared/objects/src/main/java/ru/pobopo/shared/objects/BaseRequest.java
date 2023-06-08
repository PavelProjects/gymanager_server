package ru.pobopo.shared.objects;

public class BaseRequest {
    private UserDetails userDetails;

    public BaseRequest() {
    }

    public BaseRequest(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
}
