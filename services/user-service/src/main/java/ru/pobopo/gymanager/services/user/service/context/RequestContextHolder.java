package ru.pobopo.gymanager.services.user.service.context;

import ru.pobopo.gymanager.shared.objects.AuthorizedUserInfo;

public class RequestContextHolder {
    private static String requestId;
    private static AuthorizedUserInfo currentUserInfo;

    public static String getRequestId() {
        return requestId;
    }

    public static void setRequestId(String requestId) {
        RequestContextHolder.requestId = requestId;
    }

    public static AuthorizedUserInfo getCurrentUserInfo() {
        return currentUserInfo;
    }

    public static void setCurrentUserInfo(AuthorizedUserInfo currentUserInfo) {
        RequestContextHolder.currentUserInfo = currentUserInfo;
    }
}
