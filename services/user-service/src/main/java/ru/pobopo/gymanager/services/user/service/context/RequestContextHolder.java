package ru.pobopo.gymanager.services.user.service.context;

import java.util.UUID;

public class RequestContextHolder {
    private static UUID requestUuid;

    public static UUID getRequestUuid() {
        return requestUuid;
    }

    public static String getRequestUuidString() {
        return requestUuid.toString();
    }

    public static void setRequestUuid(UUID requestUuid) {
        RequestContextHolder.requestUuid = requestUuid;
    }
}
