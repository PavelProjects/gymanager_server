package ru.pobopo.gymanager.shared.objects;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class UnprotectedPathsValidator {
    public static final String UNPROTECTED_PATHS_ENV = "UNPROTECTED_PATHS";

    private List<String> unprotectedPaths = new ArrayList<>();

    public UnprotectedPathsValidator() {
        String paths = System.getenv(UNPROTECTED_PATHS_ENV);
        if (StringUtils.isNotBlank(paths)) {
            unprotectedPaths = List.of(paths.split(";"));
        }
    }

    public boolean isUnprotectedPath(String path) {
        if (StringUtils.isBlank(path)) {
            return false;
        }

        for (String unprotected: unprotectedPaths) {
            if (StringUtils.equals(path, unprotected)) {
                return true;
            }
        }

        return false;
    }
}
