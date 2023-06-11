package ru.pobopo.gymanager.services.user.service.filter;

import static ru.pobopo.gymanager.shared.constants.HeadersNames.USER_ID_HEADER;
import static ru.pobopo.gymanager.shared.constants.HeadersNames.USER_LOGIN_HEADER;
import static ru.pobopo.gymanager.shared.constants.HeadersNames.USER_ROLES_HEADER;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.pobopo.gymanager.services.user.service.context.RequestContextHolder;
import ru.pobopo.gymanager.services.user.service.exception.AccessDeniedException;
import ru.pobopo.gymanager.shared.objects.AuthorizedUserInfo;
import ru.pobopo.gymanager.shared.objects.ErrorResponse;
import ru.pobopo.gymanager.shared.objects.UnprotectedPathsValidator;

// todo вынести куда то в общее?

@Component
@Slf4j
@Order(0)
public class SecurityFilter extends OncePerRequestFilter {
    private final Gson gson;
    private final UnprotectedPathsValidator unprotectedPathsValidator;

    @Autowired
    public SecurityFilter(Gson gson, UnprotectedPathsValidator unprotectedPathsValidator) {
        this.gson = gson;
        this.unprotectedPathsValidator = unprotectedPathsValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String userLogin = request.getHeader(USER_LOGIN_HEADER);
        String userId = request.getHeader(USER_ID_HEADER);
        String userRoles = request.getHeader(USER_ROLES_HEADER);
        try {
            if (StringUtils.isBlank(userLogin) || StringUtils.isBlank(userId) ) {
                if (unprotectedPathsValidator.isUnprotectedPath(request.getRequestURI())) {
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    throw new AccessDeniedException();
                }
            }

            AuthorizedUserInfo userInfo = new AuthorizedUserInfo(
                userId,
                userLogin,
                userRoles
            );
            RequestContextHolder.setCurrentUserInfo(userInfo);
            log.warn("Authorized user: " + userInfo);
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            processException(exception, response);
        }
    }

    private void processException(Exception exception, HttpServletResponse response) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse();
        if (exception != null) {
            errorResponse.setMessage(exception.getMessage());
            if (exception instanceof AccessDeniedException) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        response.getWriter().write(gson.toJson(errorResponse));
    }
}
