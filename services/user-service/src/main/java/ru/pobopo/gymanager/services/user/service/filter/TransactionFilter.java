package ru.pobopo.gymanager.services.user.service.filter;

import static ru.pobopo.gymanager.shared.objects.HeadersNames.CURRENT_REQUEST_ID;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.pobopo.gymanager.services.user.service.context.RequestContextHolder;
import ru.pobopo.gymanager.shared.objects.AuthorizedUserInfo;

@Slf4j
@Component
@Order(1)
public class TransactionFilter extends OncePerRequestFilter {
    private static final String LOG_REQUEST = "[{}] REQUEST ({}) [{}]::{}";
    private static final String LOG_RESPONSE = "[{}] RESPONSE ({}) FINISHED";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String requestId = request.getHeader(CURRENT_REQUEST_ID);
        RequestContextHolder.setRequestId(StringUtils.isBlank(requestId) ? UUID.randomUUID().toString() : requestId);

        AuthorizedUserInfo userInfo = RequestContextHolder.getCurrentUserInfo();
        if (userInfo == null) {
            // не смертельно, тк эти данные используются тут только для логирования
            userInfo = new AuthorizedUserInfo("USER-MISSING", "USER-MISSING", "");
        }
        log.info(LOG_REQUEST,
            RequestContextHolder.getRequestId(),
            userInfo.getUserLogin(),
            request.getMethod(),
            request.getServletPath()
        );

        filterChain.doFilter(request, response);

        log.info(LOG_RESPONSE,
            RequestContextHolder.getRequestId(),
            userInfo.getUserLogin()
        );
    }
}